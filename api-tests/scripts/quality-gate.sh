#!/bin/bash

set -u

RESULT_FILE="target/quality-gate.properties"
HTTP_5XX_FILE="target/unexpected-5xx.log"

echo "=============================="
echo "       QUALITY GATE"
echo "=============================="

if [ ! -f "$RESULT_FILE" ]; then
    echo "Resultados dos testes não encontrados."
    exit 1
fi

source "$RESULT_FILE"

GATE_FAILED=0

echo ""
echo "Total de testes: $total_tests"
echo "Testes aprovados: $passed_tests"
echo "Testes reprovados: $failed_tests"
echo "Testes críticos: $critical_total"
echo "Testes críticos aprovados: $critical_passed"

if [ "$total_tests" -eq 0 ]; then
    echo "Nenhum teste foi executado."
    exit 1
fi

echo ""
echo "Critério 1 - 100% dos testes críticos"

if [ "$critical_passed" -eq "$critical_total" ]; then
    echo "Atendido"
else
    echo "Não atendido"
    GATE_FAILED=1
fi

echo ""
echo "Critério 2 - mínimo de 90% da suíte"

if [ $((passed_tests * 100)) -ge $((total_tests * 90)) ]; then
    echo "Atendido"
else
    echo "Não atendido"
    GATE_FAILED=1
fi

echo ""
echo "Critério 3 - nenhuma resposta HTTP 5xx"

if [ -s "$HTTP_5XX_FILE" ]; then

    HTTP_5XX_COUNT=$(wc -l < "$HTTP_5XX_FILE")

    echo "Não atendido"
    echo "Respostas 5xx encontradas: $HTTP_5XX_COUNT"

    GATE_FAILED=1

else

    echo "Atendido"

fi

echo ""
echo "=============================="

if [ "$GATE_FAILED" -eq 0 ]; then

    echo "QUALITY GATE APROVADO"
    exit 0

else

    echo "QUALITY GATE REPROVADO"
    exit 1

fi