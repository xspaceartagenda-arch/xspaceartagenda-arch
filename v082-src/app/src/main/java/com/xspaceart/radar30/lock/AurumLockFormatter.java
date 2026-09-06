package com.xspaceart.radar30.lock;

/** Textos compactos usados pela tela, bolha e telemetria do app. */
public final class AurumLockFormatter {
    private AurumLockFormatter() {}

    public static String focus(AurumLockSnapshot s) {
        if (s == null || s.target < 0) return "Foco: aguardando candidato estrutural.";
        return "Foco: núcleo " + s.target + " • cobertura " + s.coverageLabel()
                + " • " + s.signalType.label + ".";
    }

    public static String ranking(AurumLockSnapshot s) {
        if (s == null || s.target < 0) return "Ranking: aguardando massa suficiente.";
        return "Ranking: Top 1 " + s.target + " • rival "
                + (s.runnerUp < 0 ? "—" : s.runnerUp)
                + " • margem " + s.margin + "/100.";
    }

    public static String curve(AurumLockSnapshot s) {
        if (s == null) return "Curva: aguardando atualização.";
        String trend = s.qTrend > 0 ? "+" + s.qTrend : String.valueOf(s.qTrend);
        return "Curva: Q" + s.q + " (Δ " + trend + ") • Readiness "
                + s.readiness + "/100 • estabilidade " + s.stableTargetUpdates + ".";
    }

    public static String families(AurumLockSnapshot s) {
        if (s == null) return "Famílias: aguardando evidência.";
        return "Famílias reais: " + s.familySummary() + " • "
                + s.independentFamilies + "/4 • provenance "
                + s.provenance.size() + " grupos.";
    }

    public static String gate(AurumLockSnapshot s) {
        if (s == null) return "Gate: aguardando veredito.";
        String blocker = AurumLockPolicy.blocker(s, AurumLockConfig.defaults());
        return "Gate: " + s.readinessState + " • Regime " + s.regime
                + " • Fast Audit " + (s.fastAuditPassed ? "PASS" : "BLOCKED")
                + " • trava: " + blocker
                + " • " + s.captureLabel
                + " • Q mede qualidade, não chance de vitória.";
    }
}
