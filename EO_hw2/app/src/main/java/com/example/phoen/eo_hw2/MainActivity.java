package com.example.phoen.eo_hw2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private double result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View v) {
        Button button = (Button)findViewById(v.getId());
        TextView textView_formula = (TextView)findViewById(R.id.textview_formula);
        String text = "";

        switch(v.getId()) {
            case R.id.button_equal:
                result = eval( textView_formula.getText().toString());
                text = "=";
                break;
            case R.id.button_multiply:
                text = "*";
                break;
            default:
                text = button.getText().toString();
                break;
        }
        textView_formula.setText(textView_formula.getText().toString() + text);
        if(v.getId() == R.id.button_equal) {
            textView_formula.setText(textView_formula.getText().toString() + result);
        }
    }

    public void onRadioButtonClick(View v) {
        TextView textView_formula = (TextView)findViewById(R.id.textview_formula);
        switch(v.getId()) {
            case R.id.radiobutton_decimal:
                if(result % 1 == 0) {
                    // if result is integer
                    textView_formula.setText(Integer.toString((int)result));
                }
                else {
                    // if result is not integer
                    textView_formula.setText(Double.toString(result));
                }
                break;
            case R.id.radiobutton_hexadecimal:
                if(result % 1 == 0) {
                    // if result is integer
                    textView_formula.setText(Integer.toHexString((int)result).toUpperCase());
                }
                else {
                    // if result is not integer
                    textView_formula.setText(Double.toHexString(result).toUpperCase());
                }
                break;
            default:
                break;
        }
    }

    // From http://stackoverflow.com/a/26227947
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
