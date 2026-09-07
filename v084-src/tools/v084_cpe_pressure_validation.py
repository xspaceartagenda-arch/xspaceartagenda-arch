#!/usr/bin/env python3
from pathlib import Path
import sys
root = Path.cwd()

def req(path, *needles):
    p = root / path
    assert p.exists(), f'missing {path}'
    text = p.read_text(encoding='utf-8')
    for n in needles:
        assert n in text, f'{path}: missing {n}'
    return text

build = req('app/build.gradle', 'versionCode 18', 'versionName "0.8.4-cpe-pressure"')
req('app/src/main/java/com/xspaceart/radar30/cpe/ConditionalPredictionEngine.java',
    'CPE v1', 'sectorPressure', 'directTransition', 'terminalTransition',
    'echoPressure', 'validatedArithmetic', '0.26, 0.20, 0.13, 0.18')
req('app/src/main/java/com/xspaceart/radar30/cpe/ConditionalPredictionSnapshot.java',
    'topNumbers', 'primarySector', 'primaryTerminal', 'sampleQuality', 'shadow')
req('app/src/main/java/com/xspaceart/radar30/AurumPressureView.java',
    'PRESSÃO CONDICIONAL', 'SETOR 1', 'TERMINAL')
req('app/src/main/java/com/xspaceart/radar30/StandaloneActivity.java',
    'MAPA DE PRESSÃO DA LEITURA', 'RAIO-X DO PRÓXIMO GIRO', 'CPE • SHADOW')
req('app/src/main/java/com/xspaceart/radar30/integrated/IntegratedActivity.java',
    'MAPA DE PRESSÃO • CPE SHADOW')
req('app/src/main/java/com/xspaceart/radar30/AppStateStore.java',
    'AURUM_CPE_TOP_NUMBERS', 'aurumCpePrimarySector', 'aurumCpeDrivers')
engine = req('app/src/main/java/com/xspaceart/radar30/lock/AurumLockEngine.java',
    'ConditionalPredictionEngine.analyze(index)', '.conditionalPrediction(cpe)')
policy = req('app/src/main/java/com/xspaceart/radar30/lock/AurumLockPolicy.java')
assert 'ConditionalPrediction' not in policy and 'CPE' not in policy, 'CPE must remain shadow in v0.8.4'
req('tools/V084ConditionalPredictionTest.java', 'CPE must be shadow in v0.8.4')
print('V084_CPE_PRESSURE_STATIC=PASS')
print('CPE_SHADOW_NO_SIGNAL_GATE_CHANGE=PASS')
print('PRESSURE_UI=PASS')
