package com.xspaceart.radar30.lock;

/**
 * Configuração central e versionada do Aurum Lock.
 *
 * Os cortes iniciais são experimentais. Eles medem qualidade estrutural e
 * maturidade operacional; nunca representam probabilidade de vitória.
 */
public final class AurumLockConfig {
    public static final String FEATURE_FLAG_NAME = "aurum_opportunity_lock_v2";
    public static final boolean ENABLED_BY_DEFAULT = true;
    public static final String ENGINE_VERSION = "aurum-lock-v2-opportunity";

    public final int historyCapacity;
    public final int minimumHistory;
    public final int preLockQ;
    public final int confirmedQ;
    public final int instantQ;
    public final int armedReadiness;
    public final int minimumIndependentFamilies;
    public final int maxValiditySpins;
    public final int echoLookback;
    public final int echoMinimumBaseQ;

    public final double physicalWeight;
    public final double numericWeight;
    public final double sequentialWeight;
    public final double patternEchoWeight;

    public AurumLockConfig(int historyCapacity,
                           int minimumHistory,
                           int preLockQ,
                           int confirmedQ,
                           int instantQ,
                           int armedReadiness,
                           int minimumIndependentFamilies,
                           int maxValiditySpins,
                           int echoLookback,
                           int echoMinimumBaseQ,
                           double physicalWeight,
                           double numericWeight,
                           double sequentialWeight,
                           double patternEchoWeight) {
        if (historyCapacity < 30) throw new IllegalArgumentException("historyCapacity must be >= 30");
        if (minimumHistory < 8 || minimumHistory > historyCapacity) {
            throw new IllegalArgumentException("minimumHistory out of range");
        }
        if (!(preLockQ <= confirmedQ && confirmedQ <= instantQ)) {
            throw new IllegalArgumentException("Q thresholds must be ordered");
        }
        if (maxValiditySpins < 1 || maxValiditySpins > 3) {
            throw new IllegalArgumentException("validity must be 1..3");
        }
        if (minimumIndependentFamilies < 1 || minimumIndependentFamilies > 4) {
            throw new IllegalArgumentException("independent families must be 1..4");
        }
        double weightSum = physicalWeight + numericWeight + sequentialWeight + patternEchoWeight;
        if (!Double.isFinite(weightSum) || weightSum <= 0.0) {
            throw new IllegalArgumentException("family weights must be positive");
        }
        this.historyCapacity = historyCapacity;
        this.minimumHistory = minimumHistory;
        this.preLockQ = clampScore(preLockQ);
        this.confirmedQ = clampScore(confirmedQ);
        this.instantQ = clampScore(instantQ);
        this.armedReadiness = clampScore(armedReadiness);
        this.minimumIndependentFamilies = minimumIndependentFamilies;
        this.maxValiditySpins = maxValiditySpins;
        this.echoLookback = Math.max(20, Math.min(historyCapacity, echoLookback));
        this.echoMinimumBaseQ = clampScore(echoMinimumBaseQ);
        this.physicalWeight = physicalWeight / weightSum;
        this.numericWeight = numericWeight / weightSum;
        this.sequentialWeight = sequentialWeight / weightSum;
        this.patternEchoWeight = patternEchoWeight / weightSum;
    }

    public static AurumLockConfig defaults() {
        return new AurumLockConfig(
                500, 12,
                54, 62, 76, 58,
                3, 3,
                180, 58,
                0.34, 0.22, 0.29, 0.15);
    }

    /** Configuração permissiva usada exclusivamente por testes determinísticos. */
    public static AurumLockConfig forTests() {
        return new AurumLockConfig(
                500, 8,
                12, 18, 28, 18,
                1, 3,
                120, 0,
                0.34, 0.22, 0.29, 0.15);
    }

    private static int clampScore(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
