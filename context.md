# 🏗️ Lógica de Negócio: DeFulo - Gestão de Inteligência Rural

## 1. O Objetivo Principal
Capacitar o Engenheiro Agrônomo a gerenciar múltiplas fazendas e talhões com precisão técnica, garantindo que o registro de decisões e diagnósticos ocorra no momento exato do campo, independente de conectividade.

## 2. Pilar Central: Offline-First
O trabalho do agrônomo acontece onde o sinal não chega.
- **Operação Contínua:** O app deve permitir cadastros, diagnósticos e manejos sem internet.
- **Sincronização Inteligente:** O backend (Java) deve ser o juiz que recebe os dados acumulados e organiza a "Memória da Terra" assim que o dispositivo sincroniza.

## 3. Hierarquia de Gestão (A Terra)
1. **Condomínio:** Gestão macro (Ex: Associação de Produtores).
2. **Fazenda:** Unidade de gestão do produtor.
3. **Talhão:** Unidade de análise técnica (onde o agrônomo faz o diagnóstico).

## 4. O Fluxo de Trabalho (A Lógica)
1. **Planejamento (Escritório):** O Agrônomo/Engenheiro define os parâmetros técnicos (Gabarito) para cada cultura.
2. **Execução (Campo/Offline):** O Agrônomo visita o talhão, realiza o diagnóstico e registra o manejo no App Flutter.
3. **Consolidação (Nuvem):** Os dados sobem para o Backend Java, gerando relatórios de produtividade e histórico para o produtor e para o condomínio.

## 5. Perfis e Responsabilidades
- **Engenheiro Agrônomo:** O gestor técnico. Define o "como fazer" (Gabarito) e supervisiona múltiplos talhões.
- **RTV (Consultor):** O operacional de campo. Executa as visitas e registra os manejos.
- **Produtor:** O beneficiário. Visualiza os relatórios e a evolução da sua terra.

## 6. Regra de Ouro
O sistema deve ser um facilitador, não um fardo. Se o registro offline for complexo, o agrônomo voltará para o papel. A simplicidade no registro é o que garante a riqueza dos dados no banco.