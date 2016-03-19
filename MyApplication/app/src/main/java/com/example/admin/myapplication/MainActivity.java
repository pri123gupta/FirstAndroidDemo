package com.example.admin.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    int item, first, last, middle;
    static int[] array = {1, 2, 3, 4, 5, 6, 7, 8};

    int valuee;

    EditText e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        e = (EditText) findViewById(R.id.value);

        Button b = (Button) findViewById(R.id.check);







        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                first = 0;
                last = array.length - 1;
                middle = (first + last)/2;

                String value = e.getText().toString();

                if(!value.equals(null) || !value.equals("")) {
                    valuee = Integer.parseInt(value);

                    item = valuee;
                }

               /* while( first <= last )
                {
                    if ( array[middle] < item )
                        first = middle + 1;
                    else if ( array[middle] == item )
                    {
                        System.out.println(item + " found at location " + (middle + 1) + ".");
                        Toast.makeText(MainActivity.this,item + " found at location " + (middle + 1),Toast.LENGTH_LONG).show();
                        break;
                    }
                    else
                    {
                        last = middle - 1;
                    }
                    middle = (first + last)/2;
                }
                if ( first > last ) {
                    System.out.println(item + " is not found.\n");

                    Toast.makeText(MainActivity.this, "not Found", Toast.LENGTH_LONG).show();

                }*/

                //factorial(item);


                Toast.makeText(MainActivity.this,String.valueOf(factorial(item)),Toast.LENGTH_LONG).show();
            }
        });






    }

    @Override
    public void onClick(View v) {

    }



    public int factorial(int n)
    {
        if(n==0)
            return 1;
        else {
           return n*factorial(n-1);
        }
    }

}
