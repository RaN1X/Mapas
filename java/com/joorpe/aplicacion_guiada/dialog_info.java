package com.joorpe.aplicacion_guiada;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;



    public class dialog_info {
        Context context;
        RespuestaDialogo rd;
        double mLatitude = 0.0, mLongitude = 0.0;

        public dialog_info(Context context, double mLatitude, double mLongitude, RespuestaDialogo rd) {
            this.context = context;
            this.rd = rd;
            this.mLatitude = mLatitude;
            this.mLongitude = mLongitude;
        }


        public Dialog MostrarDialogoBotones() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Ubicación actual:")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage("Lat: " + mLatitude + " Lon: " + mLongitude)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            rd.OnAccept("Has aceptado");
                        }
                    });
            //devuelves el builder creandolo
            return builder.create();
        }

        public interface RespuestaDialogo {
            void OnAccept(String cadena);
        }

    }

