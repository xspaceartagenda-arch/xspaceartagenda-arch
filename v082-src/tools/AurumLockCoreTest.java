import com.xspaceart.radar30.core.SignalUpdate;
import com.xspaceart.radar30.echo.PatternEchoEngine;
import com.xspaceart.radar30.lock.AurumEvidence;
import com.xspaceart.radar30.lock.AurumLockConfig;
import com.xspaceart.radar30.lock.AurumLockPolicy;
import com.xspaceart.radar30.lock.AurumLockSession;
import com.xspaceart.radar30.lock.AurumLockSnapshot;
import com.xspaceart.radar30.lock.IncrementalAurumIndex;
import com.xspaceart.radar30.lock.StructuralRegimeDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class AurumLockCoreTest {
    public static void main(String[] args) {
        incrementalIndexMatchesRebuild();
        massNormalizationAndDependencyReduction();
        deterministicReplay();
        policyIsUnidirectional();
        opportunityLockRequiresRealConfluence();
        regimeDetectsDecayAndSaturation();
        patternEchoOrderingAndSingleVote();
        sessionLockAndDynamicValidity();
        dataQualityIsMandatoryGate();
        System.out.println("AURUM_LOCK_CORE=PASS");
    }

    private static void incrementalIndexMatchesRebuild() {
        IncrementalAurumIndex incremental = new IncrementalAurumIndex(500);
        List<Integer> newest = new ArrayList<>();
        for (int i = 0; i < 620; i++) {
            int number = Math.floorMod(i * 17 + i * i * 3 + 7, 37);
            incremental.appendNewestFirst(Collections.singletonList(number));
            newest.add(0, number);
            if (newest.size() > 500) newest.remove(newest.size() - 1);
            if (i % 17 == 0 || i == 619) {
                IncrementalAurumIndex rebuilt = new IncrementalAurumIndex(500);
                rebuilt.rebuildNewestFirst(newest);
                require(incremental.invariantHolds(), "incremental invariant failed at " + i);
                require(incremental.size() == rebuilt.size(), "size differs from rebuild");
                for (int n = 0; n < 37; n++) {
                    require(incremental.count(n) == rebuilt.count(n), "global count mismatch");
                    for (int w : new int[]{5, 10, 30}) {
                        require(incremental.windowCount(w, n) == rebuilt.windowCount(w, n),
                                "window count mismatch w=" + w + " n=" + n);
                    }
                    for (int to = 0; to < 37; to++) {
                        require(incremental.transitionCount(n, to)
                                        == rebuilt.transitionCount(n, to),
                                "transition mismatch " + n + "->" + to);
                    }
                }
            }
        }
        require(incremental.size() == 500, "ring must stay bounded at 500");
        require(incremental.rebuildCount() == 0, "delta path must not rebuild history");
        require(incremental.deltaUpdates() == 620, "every append must be counted once");
    }

    private static void massNormalizationAndDependencyReduction() {
        double[] broad = new double[37];
        for (int n : new int[]{1, 2, 3, 4, 5}) broad[n] = 1.0;
        AurumEvidence.FamilyVector broadVector =
                new AurumEvidence.Builder(AurumEvidence.Family.PHYSICAL)
                        .add("broad", "physical:same", broad, 0.8, 31L, "")
                        .build(1L);
        require(close(broadVector.totalMass(), 1.0), "broad family mass must be one");

        double[] first = new double[37]; first[7] = 1.0;
        double[] duplicate = new double[37]; duplicate[22] = 10.0;
        AurumEvidence.FamilyVector reduced =
                new AurumEvidence.Builder(AurumEvidence.Family.PATTERN_ECHO)
                        .add("echo-principal", "echo:same-observation", first, 0.9, 7L, "p1")
                        .add("echo-duplicate", "echo:same-observation", duplicate, 0.4, 7L, "p1")
                        .build(2L);
        require(close(reduced.totalMass(), 1.0), "narrow family mass must be one");
        require(reduced.provenance.size() == 1, "dependent evidence must count once");
        require(reduced.at(7) > 0.999 && reduced.at(22) == 0.0,
                "weaker duplicate must not inflate or redirect evidence");
    }

    private static void deterministicReplay() {
        List<Integer> history = generatedHistory(140);
        AurumLockSession a = new AurumLockSession();
        AurumLockSession b = new AurumLockSession();
        AurumLockSession.Outcome a0 = a.replaceHistory(history, "REPLAY");
        AurumLockSession.Outcome b0 = b.replaceHistory(history, "REPLAY");
        sameSnapshot(a0.snapshot, b0.snapshot);

        List<Integer> nextHistory = new ArrayList<>(history);
        nextHistory.add(0, 26);
        AurumLockSession.Outcome a1 = a.update(nextHistory, Collections.singletonList(26), true, "REPLAY");
        AurumLockSession.Outcome b1 = b.update(nextHistory, Collections.singletonList(26), true, "REPLAY");
        sameSnapshot(a1.snapshot, b1.snapshot);
        require(a1.update.kind == b1.update.kind, "same replay must emit same state");
        require(a1.update.coverage.equals(b1.update.coverage), "same replay must emit same coverage");
    }

    private static void policyIsUnidirectional() {
        AurumLockConfig config = AurumLockConfig.defaults();
        AurumLockSnapshot instant = policyInput(90, 94, 1,
                AurumLockSnapshot.Regime.PEAK, true, true);
        AurumLockSnapshot instantOut = AurumLockPolicy.apply(instant, config, true);
        require(instantOut.lockMode == AurumLockSnapshot.LockMode.INSTANT_LOCK,
                "exceptional state must lock in the same cycle");

        AurumLockSnapshot oneUpdate = policyInput(83, 88, 1,
                AurumLockSnapshot.Regime.CONVERGENCE, true, true);
        require(AurumLockPolicy.apply(oneUpdate, config, true).lockMode
                        == AurumLockSnapshot.LockMode.NONE,
                "confirmed lock needs accumulated stability");
        AurumLockSnapshot confirmed = policyInput(83, 88, 2,
                AurumLockSnapshot.Regime.CONVERGENCE, true, true);
        require(AurumLockPolicy.apply(confirmed, config, true).lockMode
                        == AurumLockSnapshot.LockMode.CONFIRMED_LOCK,
                "stable strong state must confirm without a fixed extra delay");

        AurumLockSnapshot decay = policyInput(94, 97, 5,
                AurumLockSnapshot.Regime.DECAY, true, true);
        AurumLockSnapshot decayOut = AurumLockPolicy.apply(decay, config, true);
        require(decayOut.lockMode == AurumLockSnapshot.LockMode.NONE,
                "decay is a publication veto");
        require(decayOut.q == 94, "regime must never feed back into or alter Q");

        AurumLockSnapshot badCapture = policyInput(95, 99, 5,
                AurumLockSnapshot.Regime.PEAK, false, true);
        require(AurumLockPolicy.apply(badCapture, config, true).readinessState
                        == AurumLockSnapshot.ReadinessState.INVALIDATED,
                "data-quality failure is a hard gate, not a Q penalty");
    }

    private static void opportunityLockRequiresRealConfluence() {
        AurumLockConfig config = AurumLockConfig.defaults();

        AurumLockSnapshot visible = new AurumLockSnapshot.Builder()
                .q(59).readiness(56).stableTargetUpdates(2)
                .regime(AurumLockSnapshot.Regime.CONVERGENCE)
                .target(20).runnerUp(33).margin(11).windowConsistency(62)
                .contradiction(28).independentFamilies(4)
                .dataQualityValid(true).fastAuditPassed(true)
                .coverage(Arrays.asList(1, 20, 14, 31, 9)).build();
        require(AurumLockPolicy.apply(visible, config, true).lockMode
                        == AurumLockSnapshot.LockMode.CONFIRMED_LOCK,
                "four-family visible opportunity should be usable without Q80");

        AurumLockSnapshot onlyTwoFamilies = visible.toBuilder()
                .independentFamilies(2).build();
        require(AurumLockPolicy.apply(onlyTwoFamilies, config, true).lockMode
                        == AurumLockSnapshot.LockMode.NONE,
                "visible opportunity may not publish with only two families");

        AurumLockSnapshot unstable = visible.toBuilder()
                .stableTargetUpdates(1).build();
        require(AurumLockPolicy.apply(unstable, config, true).lockMode
                        == AurumLockSnapshot.LockMode.NONE,
                "visible opportunity still needs persistence");

        AurumLockSnapshot contradictory = visible.toBuilder()
                .contradiction(61).build();
        require(AurumLockPolicy.apply(contradictory, config, true).lockMode
                        == AurumLockSnapshot.LockMode.NONE,
                "high contradiction remains a veto");

        AurumLockSnapshot strongThree = new AurumLockSnapshot.Builder()
                .q(65).readiness(64).stableTargetUpdates(2)
                .regime(AurumLockSnapshot.Regime.CONCENTRATION)
                .target(29).runnerUp(17).margin(9).windowConsistency(57)
                .contradiction(31).independentFamilies(3)
                .dataQualityValid(true).fastAuditPassed(true)
                .coverage(Arrays.asList(22, 18, 29, 7, 28)).build();
        require(AurumLockPolicy.apply(strongThree, config, true).lockMode
                        == AurumLockSnapshot.LockMode.CONFIRMED_LOCK,
                "strong stable three-family convergence should publish");
    }

    private static void regimeDetectsDecayAndSaturation() {
        AurumLockSnapshot.Regime decay = StructuralRegimeDetector.detect(
                72, 86, 90, 7, 7, 4, 3, 12, -14,
                58, 76, 88);
        require(decay == AurumLockSnapshot.Regime.DECAY, "falling curve must be DECAY");
        AurumLockSnapshot.Regime saturation = StructuralRegimeDetector.detect(
                82, 82, 84, 7, 7, 7, 3, 10, 0,
                58, 76, 88);
        require(saturation == AurumLockSnapshot.Regime.SATURATION,
                "long plateau after peak must be SATURATION");
    }

    private static void patternEchoOrderingAndSingleVote() {
        List<Integer> h = Arrays.asList(
                1,2,3,4, 1,2,3,4, 1,2,3,5, 9,8,7, 1,2,3);
        PatternEchoEngine.Result result = PatternEchoEngine.analyze(h,
                new PatternEchoEngine.Settings(3, PatternEchoEngine.Mode.EXACT, 20, 2, 0));
        for (int i = 1; i < result.aggregates.size(); i++) {
            require(result.aggregates.get(i - 1).weightedSupport + 1e-9
                            >= result.aggregates.get(i).weightedSupport,
                    "Pattern Echo aggregates must be strongest first");
        }

        AurumLockSession session = new AurumLockSession();
        AurumLockSnapshot snapshot = session.replaceHistory(generatedHistory(180), "REPLAY").snapshot;
        int echoFamilies = 0;
        for (String family : snapshot.familyLabels) if (family.contains("Echo")) echoFamilies++;
        require(echoFamilies <= 1, "Pattern Echo must never create multiple family votes");
        require(snapshot.independentFamilies <= 4,
                "temporal/regime dimensions must not inflate independent families");
    }

    private static void sessionLockAndDynamicValidity() {
        AurumLockSession session = new AurumLockSession(AurumLockConfig.forTests());
        List<Integer> history = new ArrayList<>();
        for (int i = 0; i < 36; i++) history.add(7);
        AurumLockSession.Outcome bootstrap = session.replaceHistory(history, "OCR CONFIRMADO");
        require(bootstrap.update.kind != SignalUpdate.Kind.ENTRY,
                "bootstrap must not publish an official signal");
        if (bootstrap.update.kind == SignalUpdate.Kind.PREPARE) {
            require(bootstrap.update.speech.isEmpty(), "pre-lock must remain silent");
        }

        history.add(0, 7);
        AurumLockSession.Outcome entry = session.update(history,
                Collections.singletonList(7), true, "OCR CONFIRMADO");
        require(entry.update.kind == SignalUpdate.Kind.ENTRY,
                "concentrated deterministic fixture should arm a test lock, got " + entry.update.kind
                        + " Q" + entry.snapshot.q + " R" + entry.snapshot.readiness
                        + " regime=" + entry.snapshot.regime
                        + " families=" + entry.snapshot.independentFamilies);
        require(entry.snapshot.activeRemaining <= 3, "validity may never exceed three");

        int miss = firstOutside(entry.update.coverage);
        SignalUpdate.Kind terminalKind = entry.update.kind;
        for (int i = 0; i < 3 && (terminalKind == SignalUpdate.Kind.ENTRY
                || terminalKind == SignalUpdate.Kind.ACTIVE); i++) {
            history.add(0, miss);
            AurumLockSession.Outcome out = session.update(history,
                    Collections.singletonList(miss), true, "OCR CONFIRMADO");
            require(out.snapshot.activeRemaining <= 3, "remaining validity overflow");
            terminalKind = out.update.kind;
            if (terminalKind == SignalUpdate.Kind.INVALIDATED
                    || terminalKind == SignalUpdate.Kind.EXPIRED) break;
        }
        require(terminalKind == SignalUpdate.Kind.INVALIDATED
                        || terminalKind == SignalUpdate.Kind.EXPIRED,
                "signal must cancel or expire by the third miss, got " + terminalKind);
    }

    private static void dataQualityIsMandatoryGate() {
        AurumLockSession session = new AurumLockSession(AurumLockConfig.forTests());
        List<Integer> history = new ArrayList<>();
        for (int i = 0; i < 36; i++) history.add(7);
        session.replaceHistory(history, "OCR CONFIRMADO");
        history.add(0, 7);
        session.update(history, Collections.singletonList(7), true, "OCR CONFIRMADO");
        AurumLockSession.Outcome blocked = session.blockDataQuality("conflito DOM/OCR");
        require(!blocked.snapshot.dataQualityValid, "blocked capture must be explicit");
        require(blocked.snapshot.q == 0, "invalid capture must not become a lower Q");
        require(blocked.update.kind == SignalUpdate.Kind.INVALIDATED,
                "active signal must cancel on invalid capture");
    }

    private static AurumLockSnapshot policyInput(int q, int readiness, int stable,
                                                 AurumLockSnapshot.Regime regime,
                                                 boolean dataQuality,
                                                 boolean audit) {
        return new AurumLockSnapshot.Builder()
                .q(q).readiness(readiness).stableTargetUpdates(stable)
                .regime(regime).target(7).runnerUp(20).margin(18).windowConsistency(64)
                .contradiction(20).independentFamilies(3).dataQualityValid(dataQuality)
                .fastAuditPassed(audit).coverage(Arrays.asList(29, 7, 28, 12, 35))
                .build();
    }

    private static List<Integer> generatedHistory(int size) {
        List<Integer> chronological = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            chronological.add(Math.floorMod(i * 11 + (i / 5) * 7 + 3, 37));
        }
        Collections.reverse(chronological);
        return chronological;
    }

    private static int firstOutside(List<Integer> coverage) {
        for (int n = 0; n < 37; n++) if (!coverage.contains(n)) return n;
        throw new AssertionError("coverage unexpectedly contains every number");
    }

    private static void sameSnapshot(AurumLockSnapshot a, AurumLockSnapshot b) {
        require(a.q == b.q, "deterministic Q mismatch");
        require(a.readiness == b.readiness, "deterministic readiness mismatch");
        require(a.regime == b.regime, "deterministic regime mismatch");
        require(a.readinessState == b.readinessState, "deterministic state mismatch");
        require(a.lockMode == b.lockMode, "deterministic mode mismatch");
        require(a.target == b.target && a.runnerUp == b.runnerUp,
                "deterministic ranking mismatch");
        require(a.coverage.equals(b.coverage), "deterministic coverage mismatch");
        require(a.familyStrengths.equals(b.familyStrengths),
                "deterministic family diagnostics mismatch");
        require(a.historyFingerprint == b.historyFingerprint,
                "deterministic fingerprint mismatch");
    }

    private static boolean close(double a, double b) { return Math.abs(a - b) < 1e-9; }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
