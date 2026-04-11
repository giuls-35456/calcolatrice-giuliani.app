package com.example.calcolatrice_giuliani;

import android.content.res.Configuration; // Necessario per controllare l'orientamento
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView displayResult;
    private TextView displayExpression;

    private String currentInput = "";
    private String operator = "";
    private double firstOperand = 0;
    private boolean isNewInput = false;
    private boolean hasResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inizializziamo i display (presenti in entrambi i layout)
        displayResult     = findViewById(R.id.displayResult);
        displayExpression = findViewById(R.id.displayExpression);

        // 1. CONTROLLO ORIENTAMENTO
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // SE VERTICALE: Colleghiamo tutta la logica dei bottoni
            initCalculatorLogic();
        } else {
            // SE ORIZZONTALE: Solo estetica
            if (displayResult != null) {
                displayResult.setText("Sola lettura");
            }
            // Non chiamando initCalculatorLogic(), i bottoni non avranno Listener
            // e quindi non faranno nulla al click.
        }
    }

    /**
     * Metodo che associa i click listener ai bottoni.
     * Viene eseguito solo quando il telefono è in verticale.
     */
    private void initCalculatorLogic() {
        // Gestione numeri
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };
        for (int id : numberIds) {
            Button b = findViewById(id);
            if (b != null) {
                b.setOnClickListener(v -> onNumber(((Button) v).getText().toString()));
            }
        }

        // Operatori e funzioni (con controllo null-safety per sicurezza)
        setSafeClickListener(R.id.btnDot, v -> onDot());
        setSafeClickListener(R.id.btnPlus, v -> onOperator("+"));
        setSafeClickListener(R.id.btnMinus, v -> onOperator("−"));
        setSafeClickListener(R.id.btnMult, v -> onOperator("×"));
        setSafeClickListener(R.id.btnDiv, v -> onOperator("÷"));
        setSafeClickListener(R.id.btnEquals, v -> onEquals());
        setSafeClickListener(R.id.btnClear, v -> onClear());
        setSafeClickListener(R.id.btnBackspace, v -> onBackspace());
        setSafeClickListener(R.id.btnPlusMinus, v -> onPlusMinus());
        setSafeClickListener(R.id.btnPercent, v -> onPercent());
    }

    // Funzione di utilità per evitare crash se un ID non esiste nel layout orizzontale
    private void setSafeClickListener(int id, View.OnClickListener listener) {
        View v = findViewById(id);
        if (v != null) v.setOnClickListener(listener);
    }

    // --- LOGICA DI CALCOLO (I tuoi metodi originali) ---

    private void onNumber(String digit) {
        if (isNewInput || hasResult) {
            currentInput = "";
            isNewInput = false;
            hasResult = false;
        }
        if (currentInput.equals("0")) currentInput = "";
        currentInput += digit;
        displayResult.setText(currentInput);
    }

    private void onDot() {
        if (isNewInput || hasResult) {
            currentInput = "0";
            isNewInput = false;
            hasResult = false;
        }
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) currentInput = "0";
            currentInput += ".";
            displayResult.setText(currentInput);
        }
    }

    private void onOperator(String op) {
        if (!currentInput.isEmpty()) {
            if (!operator.isEmpty() && !isNewInput) {
                calculate();
            } else {
                firstOperand = parseInput();
            }
        }
        operator = op;
        displayExpression.setText(format(firstOperand) + " " + op);
        isNewInput = true;
        hasResult = false;
    }

    private void onEquals() {
        if (operator.isEmpty() || currentInput.isEmpty()) return;
        double second = parseInput();
        displayExpression.setText(format(firstOperand) + " " + operator + " " + format(second) + " =");
        calculate();
        operator = "";
        hasResult = true;
    }

    private void calculate() {
        double second = parseInput();
        double result;
        switch (operator) {
            case "+": result = firstOperand + second; break;
            case "−": result = firstOperand - second; break;
            case "×": result = firstOperand * second; break;
            case "÷":
                if (second == 0) {
                    displayResult.setText("Errore");
                    currentInput = "";
                    operator = "";
                    return;
                }
                result = firstOperand / second;
                break;
            default: return;
        }
        firstOperand = result;
        currentInput = format(result);
        displayResult.setText(currentInput);
        isNewInput = true;
    }

    private void onClear() {
        currentInput = "";
        operator = "";
        firstOperand = 0;
        isNewInput = false;
        hasResult = false;
        displayResult.setText("0");
        displayExpression.setText("");
    }

    private void onBackspace() {
        if (hasResult || isNewInput) return;
        if (currentInput.length() > 1) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            currentInput = "";
        }
        displayResult.setText(currentInput.isEmpty() ? "0" : currentInput);
    }

    private void onPlusMinus() {
        if (currentInput.isEmpty() || currentInput.equals("0")) return;
        if (currentInput.startsWith("-")) {
            currentInput = currentInput.substring(1);
        } else {
            currentInput = "-" + currentInput;
        }
        displayResult.setText(currentInput);
    }

    private void onPercent() {
        if (currentInput.isEmpty()) return;
        double val = parseInput() / 100.0;
        currentInput = format(val);
        displayResult.setText(currentInput);
    }

    private double parseInput() {
        try { return Double.parseDouble(currentInput); }
        catch (NumberFormatException e) { return 0; }
    }

    private String format(double val) {
        if (val == (long) val) return String.valueOf((long) val);
        return String.valueOf(val);
    }
}