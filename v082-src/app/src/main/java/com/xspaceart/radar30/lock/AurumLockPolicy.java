package com.xspaceart.radar30.lock;

/**
 * Política de publicação do Aurum Opportunity Lock v2.
 *
 * Mantém os vetos estruturais e o Fast Audit, mas evita exigir níveis de Q
 * quase inalcançáveis em sessão real. A publicação continua dependendo de
 * múltiplas famílias independentes, estabilidade, margem e consistência.
 */
public final class AurumLockPolicy {
    private AurumLockPolicy() {}

    public static AurumLockSnapshot apply(AurumLockSnapshot input,
                                          AurumLockConfig config,
                                          boolean publicationAllowed) {
        AurumLockSnapshot.Builder out = input.toBuilder()
                .lockMode(AurumLockSnapshot.LockMode.NONE);

        if (!input.dataQualityValid) {
            return out.readinessState(AurumLockSnapshot.ReadinessState.INVALIDATED).build();
        }
        if (input.target < 0 || input.q < 40) {
            return out.readinessState(AurumLockSnapshot.ReadinessState.IDLE).build();
        }
        if (input.regime == AurumLockSnapshot.Regime.DECAY) {
            return out.readinessState(AurumLockSnapshot.ReadinessState.DECAY).build();
        }
        if (input.regime == AurumLockSnapshot.Regime.SATURATION) {
            return out.readinessState(AurumLockSnapshot.ReadinessState.SATURATED).build();
        }
        if (input.regime == AurumLockSnapshot.Regime.TRANSITION) {
            return out.readinessState(AurumLockSnapshot.ReadinessState.FORMING).build();
        }

        boolean diverse = input.independentFamilies >= config.minimumIndependentFamilies;
        boolean qualityFloor = input.fastAuditPassed && diverse
                && input.q >= config.confirmedQ
                && input.readiness >= config.armedReadiness
                && input.margin >= 6
                && input.windowConsistency >= 45
                && input.contradiction <= 55;

        // Oportunidade visível: exige TODAS as quatro famílias independentes.
        // É a ponte entre um radar claramente convergente e o Lock tradicional.
        // Não usa relógio/cadência como gatilho e não publica em dispersão.
        boolean visibleOpportunity = input.fastAuditPassed
                && input.independentFamilies >= 4
                && input.q >= Math.max(58, config.preLockQ)
                && input.readiness >= Math.max(54, config.preLockQ)
                && input.stableTargetUpdates >= 2
                && input.margin >= 8
                && input.windowConsistency >= 50
                && input.contradiction <= 45
                && input.qTrend >= -3
                && (input.regime == AurumLockSnapshot.Regime.CONCENTRATION
                || input.regime == AurumLockSnapshot.Regime.CONVERGENCE
                || input.regime == AurumLockSnapshot.Regime.PEAK);

        boolean armed = qualityFloor || visibleOpportunity;

        if (publicationAllowed && qualityFloor
                && input.q >= config.instantQ
                && input.readiness >= Math.max(66, config.armedReadiness)
                && input.regime == AurumLockSnapshot.Regime.PEAK) {
            return out.readinessState(AurumLockSnapshot.ReadinessState.LOCKED)
                    .lockMode(AurumLockSnapshot.LockMode.INSTANT_LOCK).build();
        }

        if (publicationAllowed && armed
                && input.stableTargetUpdates >= 2
                && (input.regime == AurumLockSnapshot.Regime.CONVERGENCE
                || input.regime == AurumLockSnapshot.Regime.PEAK
                || input.regime == AurumLockSnapshot.Regime.CONCENTRATION)) {
            return out.readinessState(AurumLockSnapshot.ReadinessState.LOCKED)
                    .lockMode(AurumLockSnapshot.LockMode.CONFIRMED_LOCK).build();
        }

        if (armed) return out.readinessState(AurumLockSnapshot.ReadinessState.ARMED).build();
        if (input.q >= config.preLockQ
                && input.readiness >= Math.max(48, config.preLockQ - 4)
                && input.independentFamilies >= 2
                && input.fastAuditPassed) {
            return out.readinessState(AurumLockSnapshot.ReadinessState.PRE_LOCK).build();
        }
        return out.readinessState(AurumLockSnapshot.ReadinessState.FORMING).build();
    }

    /** Explica o principal motivo que ainda impede a publicação. */
    public static String blocker(AurumLockSnapshot input, AurumLockConfig config) {
        if (input == null) return "aguardando leitura";
        if (!input.dataQualityValid) return "qualidade da captura";
        if (input.target < 0) return "sem alvo estrutural";
        if (!input.fastAuditPassed) return "Fast Audit";
        if (input.regime == AurumLockSnapshot.Regime.TRANSITION) return "alvo em transição";
        if (input.regime == AurumLockSnapshot.Regime.DECAY) return "estrutura em queda";
        if (input.regime == AurumLockSnapshot.Regime.SATURATION) return "estrutura saturada";
        if (input.regime == AurumLockSnapshot.Regime.DISPERSION) return "confluência ainda dispersa";
        if (input.independentFamilies < config.minimumIndependentFamilies)
            return "famílias " + input.independentFamilies + "/" + config.minimumIndependentFamilies;
        if (input.stableTargetUpdates < 2) return "aguardando estabilidade 2x";
        if (input.q < config.confirmedQ) return "Q " + input.q + "/" + config.confirmedQ;
        if (input.readiness < config.armedReadiness)
            return "Readiness " + input.readiness + "/" + config.armedReadiness;
        if (input.margin < 6) return "margem sobre rival baixa";
        if (input.windowConsistency < 45) return "consistência 5/10/30 baixa";
        if (input.contradiction > 55) return "contradição alta";
        return "aguardando novo giro validado";
    }
}
