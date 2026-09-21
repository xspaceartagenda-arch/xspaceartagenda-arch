# Aurum AI 37 v0.9.1 — checkpoint final de build

Atualizado em: 2026-09-21

## Resultado

O pacote consolidado de performance térmica/latência e inteligência quantitativa
foi compilado integralmente no GitHub Actions. O APK assinado passou nas auditorias
de package, versão, launcher, assinatura, integridade ZIP e alinhamento de 16 KB.

## Fonte canônica

- Repositório: `xspaceartagenda-arch/xspaceartagenda-arch`
- Branch: `main`
- Fonte completa: `Aurum-AI-37-v0.9.1-MASTER-HARD-EXPERT-GITHUB-BUILD.zip`
- SHA-256 da fonte: `e05734607980835b33b13b052d99207d4c7b7a371f59f8dd0a319c374cf39c61`
- Commit do build aprovado: `0b1b3027da220cb78f94ec0fdea023d11c907377`
- Workflow: `.github/workflows/build-aurum-v091.yml`
- Execução aprovada: `35550857708`
- Artefato GitHub: `10618336703`

## APK final

- Arquivo: `Aurum-AI-37-v0.9.1-Master-Hard-Expert-FINAL.apk`
- Tamanho: `46.685.427 bytes`
- SHA-256: `7f07190f4aa46d1e3e8ed47c36b31963038a2a21dc21dc02900ff5b54696c22c`
- Package: `com.xspaceart.aurumai37.integratedlab.debug`
- Version code: `22`
- Version name: `0.9.1-master-hard-expert-startup-fix-debug`
- Launcher: `com.xspaceart.radar30.StandaloneActivity`

## Verificações aprovadas

- Validação estática Master Hard Expert.
- Testes host em Java 17.
- Gradle `clean assembleDebug`.
- Assinatura estável do projeto.
- `zipalign -c -P 16 4`.
- OCR single-flight e throttle.
- captura térmica adaptativa.
- Wheel Spatial Kernel.
- filtro de entropia/confluência.
- projeção temporal de 2–3 giros.

## Continuidade

Não reiniciar da base anterior. Para qualquer evolução futura, partir do ZIP de
fonte completa acima e deste checkpoint. Não há pendência de código ou build;
resta apenas a validação física no aparelho alvo.
