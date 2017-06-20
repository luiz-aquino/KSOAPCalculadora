package com.example.calculadora.ksoapcalculadora;

import android.os.AsyncTask;
import android.renderscript.Double2;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText edtNum1;
    private EditText edtNum2;
    private TextView tvResult;

    Integer valNum1;
    Integer valNum2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNum1 = (EditText) findViewById(R.id.edtNum1);
        edtNum2 = (EditText) findViewById(R.id.edtNum2);
        tvResult = (TextView) findViewById(R.id.tvResutado);
    }

    public void sum(View v){

        try {
            valNum1= Integer.parseInt(edtNum1.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Valor 1 inválido", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            valNum2 = Integer.parseInt(edtNum2.getText().toString());
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Valor 2 inválido", Toast.LENGTH_LONG).show();
            return;
        }

        CalculateAsync calculate = new CalculateAsync();

        double result = 0;

        try {
            result = calculate.execute(valNum1, valNum2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        tvResult.setText(String.valueOf(result));
    }

    private class CalculateAsync extends AsyncTask<Integer, Integer,Integer> {

        String Method_Name = "sum";
        String SOAP_ACTION = "";

        String NAMESPACE = "http://luizaquino.com.br/";
        String SOAP_URL = "http://10.3.1.28:8080/Calculadora/Calculadora";

        @Override
        protected Integer doInBackground(Integer... params) {
            SoapObject request = new SoapObject(NAMESPACE, Method_Name);
            request.addProperty("num1", params[0]);
            request.addProperty("num2", params[1]);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransportSE = new HttpTransportSE(SOAP_URL);

            SoapPrimitive result = null;
            try{
                httpTransportSE.call(SOAP_ACTION, envelope);
                result = (SoapPrimitive) envelope.getResponse();
            } catch (HttpResponseException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Integer.parseInt(result != null ? result.toString() : "0");
        }
    }
}
