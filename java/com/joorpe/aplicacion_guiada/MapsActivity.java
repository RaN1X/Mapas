package com.joorpe.aplicacion_guiada;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private FusedLocationProviderClient mFusedLocationClient;
    private double mLatitude = 0.0, mLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    Drawer mDrawer;
    private GoogleMap mMap;
    private List<Marker> mPosiciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        //materialdrawer


        //Añadir MaterialDrawer totalmente vacío
        new DrawerBuilder().withActivity(this).build();



        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.ic_launcher)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName("myMapas v1.0")
                                .withEmail("jroperalta@gmail.com")
                                .withIcon(getResources().getDrawable(R.mipmap.android_icon))
                )
                .build();


        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .withDrawerGravity(Gravity.START)
                .withSliderBackgroundColor(getResources().getColor(android.R.color.darker_gray))
                .withSelectedItem(2)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(1)
                                .withName("Ver mi localizacion"),
                        new SecondaryDrawerItem()
                                .withIdentifier(2)
                                .withName("Ocultar mi localizacion"),

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem()
                                .withIdentifier(3)
                                .withName("Ver Restaurantes"),
                        new PrimaryDrawerItem()
                                .withIdentifier(4)
                                .withName("Ver Bancos"),
                        new SecondaryDrawerItem()
                                .withIdentifier(5)
                                .withName("Ocultar todo"),

                        new DividerDrawerItem(),

                        new SecondaryDrawerItem()
                                .withIdentifier(6)
                                .withName("Cerrar menu"),
                        new SecondaryDrawerItem()
                                .withIdentifier(7)
                                .withName("Salir App")

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1: {
                                Toast.makeText(MapsActivity.this,"Mi localizacion",Toast.LENGTH_LONG).show();

                                LatLng loc_latitud = new LatLng(mLatitude, mLongitude);
                                Marker loc_marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.fotico)).position(loc_latitud).title("Mi localizacion"));
                                Log.i("DEBUG",""+mLatitude+""+mLongitude);
                                loc_marker.setTag("miposicion");
                                mPosiciones.add(loc_marker);
                                requestLocations();

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_latitud));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                            break;
                            }

                            case 2: {
                                for (Marker POS:mPosiciones){
                                    POS.remove();
                                }
                                removeLocations();

                                break;
                            }

                            case 3: {

                                ver_makers("Restaurante");
                                mMap.setOnInfoWindowClickListener(MapsActivity.this);
                                break;
                            }

                            case 4: {

                                ver_makers("Banco");
                                mMap.setOnInfoWindowClickListener(MapsActivity.this);
                                break;
                            }

                            case 5: {
                                mMap.clear();
                                break;
                            }

                            case 6: {
                            Toast.makeText(MapsActivity.this,"das",Toast.LENGTH_LONG).show();
                            }
                            break;
                            case 7: {
                                finish();
                                break;
                            }
                        }
                        return false;
                    }
                }).build();








        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        //: Debido a que la ubicación precisa consume mucho, se ha escogido un consumo balanceado, esto es
        //configurable mediante LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(16000);///
        locationRequest.setFastestInterval(8000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();
                        Toast.makeText(MapsActivity.this, "Lat: " +
                                mLatitude + " Lon: " +
                                mLongitude, Toast.LENGTH_LONG).show();
                    }
                }


            }
        };
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng ayuntamiento = new LatLng(39.3626583,-0.4575496);

        //Marker markayuntamiento = mMap.addMarker(new MarkerOptions().position(ayuntamiento).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Ayuntamiento").snippet("Habitantes:250000"));
       // markayuntamiento.setTag("Ayuntamiento");

        //estas 2 ordenes situan el lugar y el zoom inicial
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ayuntamiento));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12.0f));

        //estas orden es como la de arriva pero en 1 orden
            //LatLngBounds MIZONA = new LatLngBounds(new LatLng(37.5, -3), new LatLng(41.5, 2));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MIZONA.getCenter(),7.5f));

       // Al mismo tiempo, el zoom mínimo y máximo se puede bloquear de la siguiente manera.
        mMap.setMinZoomPreference(5.0f);
        mMap.setMaxZoomPreference(18.0f);

        //Asignar un escuchador al movimiento cámara, ideal para guardar el zoom.

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle(){
                Toast.makeText(MapsActivity.this, "Zoom cambiado a: " + mMap.getCameraPosition().zoom,
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Configura el tipo de mapa
            //mMap.setMapType(GoogleMap.MAP_TYPE_NONE);

        //Permite configurar varios aspectos gráficos
        //EN ESTE CASO CONTOL DE ZOOM
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //EN ESTE CASO VER LA BRUJULITA DE ARRIVA IZQ
        mMap.getUiSettings().setCompassEnabled(true);
        //EN ESTE CASO GESTOS TACTILES PARA ROTAR MAPA
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        //EN ESTE CASO ACTIVAR O DESACTIVAR LOS ICONOS DE ABAJO DERECHA
        mMap.getUiSettings().setMapToolbarEnabled(false);

    //LISTENER PARA EL CUADRO DE ARRIVA DEL ICONO
        mMap.setOnInfoWindowClickListener(this);
     //LISTENER PARA EL PROPIO MAPA
       mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               /* String titulos=" ";
                List<Localizacion> mLocalizaciones;
                mLocalizaciones=leerLocalizaciones();
                for (Localizacion localizacion : mLocalizaciones){
                    titulos += "- " + localizacion.getTitulo() + " ";
                }
                Toast.makeText(MapsActivity.this, "Títulos: " + titulos, Toast.LENGTH_SHORT).show();*/
            }
        });

        // » .animateCamera() => Permite crear animaciones durante el posicionamiento de la cámara.
        //        Ejemplo: Simula “volar” entre un punto y otro (-_-)

           /* LatLng SYDNEY = new LatLng(-33.88,151.21);
            LatLng MOUNTAIN_VIEW= new LatLng(37.4, -122.1);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15)); //Inicio con 15 de zoom
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null); //5 segundos
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(MOUNTAIN_VIEW) //Destino final
                    .zoom(17) //Nuevo zoom final
                    .bearing(0) //Orientación de la cámara al este
                    .tilt(0) //Cámara a 30 grados
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

        //» .setMapStyle() => Permite importar desde JSON o XML estilos de mapas personalizados (Custom map)




       //---------------------------Comentamos esta forma de localizacion ya que usaremos la de Google Location Services API

        //» .setMyLocationEnabled(true); => Activa un icono en la parte superior derecha, que al pulsarlo permite a la
        //Api de Google Maps encontrarnos
    /*
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this, new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        //----------------------------

    */
        /*  Location parqueMeridiano = new Location("parqueMeridiano");
        parqueMeridiano.setLatitude(40.0);
        parqueMeridiano.setLongitude(0.0);
        Toast.makeText(MapsActivity.this, "Distancia: " + String.format("%.2f", location.distanceTo(parqueMeridiano)/1000) + " Km", Toast.LENGTH_LONG).show();
        */
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

            if(marker.getTag().toString().equals("Restaurante")) {
            Intent intent_web = new Intent(Intent.ACTION_VIEW, Uri.parse(marker.getSnippet()));
            startActivity(intent_web);
            }

            if(marker.getTag().toString().equals("Banco")){
                Intent intent_llamada = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: "+marker.getSnippet()));
                if (intent_llamada.resolveActivity(getPackageManager()) != null){
                    startActivity(intent_llamada);
                }
             }


        if(marker.getTag().toString().equals("miposicion")){
            dialog_info md = new dialog_info(this, mLatitude, mLongitude, new dialog_info.RespuestaDialogo() {
                @Override
                public void OnAccept(String cadena) {
                    Toast.makeText(MapsActivity.this, cadena, Toast.LENGTH_SHORT).show();
                }
            });

            md.MostrarDialogoBotones().show();
        }
        //Toast.makeText(MapsActivity.this, "Tit(1): " + leerLocalizaciones().get(1).getTitulo(), Toast.LENGTH_SHORT).show();

    }

//pedir persmisos de location
    private void requestLocations(){
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        } else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void removeLocations(){

        mFusedLocationClient.removeLocationUpdates(locationCallback);

    }


/*
    public List<Localizacion> leerLocalizaciones() {
        List<Localizacion> localizaciones = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getResources().openRawResource(R.raw.localizaciones));
            Element raiz = doc.getDocumentElement();
            NodeList items = raiz.getElementsByTagName("localizacion");
            for( int i = 0; i < items.getLength(); i++ ) { //recorre todos los elementos
                Node nodoLocalizacion = items.item(i);
                Localizacion localizacion = new Localizacion();
                for(int j = 0; j < nodoLocalizacion.getChildNodes().getLength(); j++ ) {//recorre hijos
                    Node nodoActual =
                            nodoLocalizacion.getChildNodes().item(j);
//comprueba si es un elemento
                    if( nodoActual.getNodeType() ==
                            Node.ELEMENT_NODE ) {
                        if( nodoActual.getNodeName().equalsIgnoreCase("titulo") )
                            localizacion.setTitulo(nodoActual.getChildNodes().item(0)
                                    .getNodeValue());
                        else if( nodoActual.getNodeName().equalsIgnoreCase("fragmento") )
                            localizacion.setFragmento(nodoActual.getChildNodes().item(0)
                                    .getNodeValue());
                            else if( nodoActual.getNodeName().equalsIgnoreCase("etiqueta") )
                                    localizacion.setEtiqueta(nodoActual.getChildNodes().item(0)
                                            .getNodeValue());
                            else if( nodoActual.getNodeName().equalsIgnoreCase("latitud ") ) {
                                        String latitud = nodoActual.getChildNodes().item(0).getNodeValue();
                                        localizacion.setLatitud(Double.parseDouble(latitud));
                            }
                             else if( nodoActual.getNodeName().equalsIgnoreCase("longitud") ) {
                                String longitud = nodoActual.getChildNodes().item(0)
                                .getNodeValue();
                                localizacion.setLongitud(Double.parseDouble(longitud));
                                }
                    }
                    }//fin for 2 (hijos)
                    localizaciones.add(localizacion);
                    }//fin for 1 (elementos)
                    } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    } catch (IOException e) {
                    e.printStackTrace();
                    } catch (SAXException e) {
                    e.printStackTrace();
                    }
                    return localizaciones;




     }//fin leerLocalizaciones

*/
    public List<Localizacion> leerLocalizaciones() {
        List<Localizacion> localizaciones = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getResources().openRawResource(R.raw.localizaciones));
            Element raiz = doc.getDocumentElement();
            NodeList items = raiz.getElementsByTagName("localizacion");

            for (int i = 0; i < items.getLength(); i++) {
                Node nodoLocalizacion = items.item(i);
                Localizacion localizacion = new Localizacion();

                for (int j = 0; j < nodoLocalizacion.getChildNodes().getLength() - 1; j++) {
                    Node nodoActual = nodoLocalizacion.getChildNodes().item(j);
                    if (nodoActual.getNodeType() == Node.ELEMENT_NODE) {
                        if (nodoActual.getNodeName().equalsIgnoreCase("titulo")) {
                            localizacion.setTitulo(nodoActual.getChildNodes().item(0).getNodeValue());
                        } else if (nodoActual.getNodeName().equalsIgnoreCase("fragmento")) {
                            localizacion.setFragmento(nodoActual.getChildNodes().item(0).getNodeValue());
                        } else if (nodoActual.getNodeName().equalsIgnoreCase("etiqueta")) {
                            localizacion.setEtiqueta(nodoActual.getChildNodes().item(0).getNodeValue());
                        } else if (nodoActual.getNodeName().equalsIgnoreCase("latitud")) {
                            String latitud = nodoActual.getChildNodes().item(0).getNodeValue();
                            localizacion.setLatitud(Double.parseDouble(latitud));
                        } else if (nodoActual.getNodeName().equalsIgnoreCase("longitud")) {
                            String longitud = nodoActual.getChildNodes().item(0).getNodeValue();
                            localizacion.setLongitud(Double.parseDouble(longitud));
                        }
                    }
                }
                localizaciones.add(localizacion);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localizaciones;
    }









    public void ver_makers(String etiqueta){
        List<Localizacion> arayxml;
        arayxml = leerLocalizaciones();
        for (Localizacion localizacion : arayxml) {
            if (etiqueta.equals("Restaurante")){
                if (localizacion.getEtiqueta().equals("Restaurante")) {
                    LatLng restaurante_pos = new LatLng(localizacion.getLatitud(), localizacion.getLongitud());
                    Marker restaurante_loc = mMap.addMarker(new MarkerOptions().position(restaurante_pos).title(localizacion.getTitulo()).snippet(localizacion.getFragmento()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.restaurante)));
                    restaurante_loc.setTag(localizacion.getEtiqueta());
                }
            }else if (etiqueta.equals("Banco")){
                if (localizacion.getEtiqueta().equals("Banco")) {
                    LatLng banco_pos = new LatLng(localizacion.getLatitud(), localizacion.getLongitud());
                    Marker banco_loc = mMap.addMarker(new MarkerOptions().position(banco_pos).title(localizacion.getTitulo()).snippet(localizacion.getFragmento()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.banco)));
                    banco_loc.setTag(localizacion.getEtiqueta());
                }
            }

        }
    }






}