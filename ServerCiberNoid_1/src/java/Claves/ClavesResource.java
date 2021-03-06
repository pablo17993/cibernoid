/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Claves;

import Admin.CoordResource;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import static java.time.Clock.system;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * REST Web Service
 *
 * @author Sol
 */
@Path("claves")
public class ClavesResource {

    @Context
    private UriInfo context;
    //private int[][] listaUsers = {{1,1000},{2,1000},{3,0},{4,2000},{5,2000}};
    //HashMap<Integer, String> mapHora = new HashMap<>();
    //private HashMap<Integer, String[]> mapHora = new HashMap<>();
    private float latitud;
    private float longitud;
    private int radio;
    private int numDecimalesLat = 2;
    private int numDecimalesLon = 2;
    private boolean redondearLon = false;
    private boolean redondearLat = false;
    private Random rnd = new Random();
    private ArrayList<String> wifisFijas = new ArrayList<String>();
    private ArrayList<String> wifisPotencias = new ArrayList<String>();

    /**
     * Creates a new instance of ClavesResource
     * public String getJson(@FormParam("operador")String operador,@FormParam("idFile")int idFile, 
           @FormParam("idUser")int idUser,@FormParam("grupo")boolean grupo,@FormParam("lat")double lat, 
           @FormParam("lon")double lon, @FormParam("hora")String hora, @FormParam("fecha")String fecha,
           @FormParam("timeMask")String timeMask,@FormParam("dateMask")String dateMask,
           @FormParam("wifis")String wifis) throws SQLException, URISyntaxException, ClassNotFoundException{
        //TODO return proper representation object
     */
    public ClavesResource() {
        //String[] dep1000={"9:00","4", "2"};
        //String[] dep2000={"10:00","8", "4"};
        //mapHora.put(1000, dep1000);
        //mapHora.put(2000, dep2000);
    }

    /**
     * Retrieves representation of an instance of Claves.ClavesResource
     * @return an instance of java.lang.String
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
           /*public String getJson(@FormParam("operador")String operador,@FormParam("idFile")int idFile, 
           @FormParam("idUser")int idUser,@FormParam("grupo")boolean grupo,@FormParam("lat")double latitud, 
           @FormParam("lon")double longitud, @FormParam("hora")String hora, @FormParam("fecha")String fecha,
           @FormParam("timeMask")String timeMask,@FormParam("dateMask")String dateMask,
           @FormParam("wifis")String wifis) throws SQLException, URISyntaxException, ClassNotFoundException{*/
    public String getJson(Parametros parametros) throws SQLException, URISyntaxException, ClassNotFoundException{
        //TODO return proper representation object
        
        Integer idFile = parametros.idFile;
        Integer idUser = parametros.idUser;
        boolean grupo = parametros.grupo;
        String operador = parametros.operador;
        float longitud = parametros.lon;
        float latitud = parametros.lat;
        String hora = parametros.hora;
        String dateMask = parametros.dateMask;
        String fecha = parametros.fecha;
        String timeMask = parametros.timeMask;
        String wifis = parametros.wifis;
        
        boolean userRegistrado=false;
        Fichero fichero = new Fichero(0,0,"hola","estoy aqui","a esta hora","en esta fecha","con estas wifis");
        try{
        Calendar ahora = Calendar.getInstance();
        int grupoBBDD =0;
        double lat=0;
        double lon=0;
        String horaBBDD="8:00";
        String timeMaskBBDD="4";
        String dateMaskBBDD="4";
        
        ahora.add(Calendar.HOUR, 1);//Cambio de hora por el servidor de Heroku
        Connection connection = null;
        try{
            Class.forName("org.postgresql.Driver");

                String url ="jdbc:postgresql://localhost:5432/postgres";
                String usuario="postgres";
                String contraseña="123";

            connection = DriverManager.getConnection(url, usuario, contraseña);
        
            if(!connection.isClosed()){
                Statement stmt = connection.createStatement();
                ResultSet rs= stmt.executeQuery("SELECT Id, Departamento FROM Usuarios");
                while(rs.next()){
                    if((rs.getInt("Id"))==idUser){
                        userRegistrado=true;
                        grupoBBDD=rs.getInt("Departamento");
                        if(grupo && grupoBBDD!=0){
                            Statement stmt2 = connection.createStatement();
                            ResultSet rs2= stmt2.executeQuery("SELECT * FROM Departamentos WHERE Id='" +Integer.toString(grupoBBDD)+"'");  
                            while(rs2.next()){
                                horaBBDD=rs2.getString("Horario");
                                timeMaskBBDD=rs2.getString("Mascara_hora");
                                dateMaskBBDD=rs2.getString("Mascara_fecha");
                                break;
                            }
                            rs2.close();
                            stmt2.close();
                        }
                        Statement stmt3 = connection.createStatement();
                        ResultSet rs3= stmt3.executeQuery("SELECT ssid, potencia FROM wifis");
                        while(rs3.next()){
                            wifisFijas.add(rs3.getString("ssid"));
                            wifisPotencias.add(Integer.toString(rs3.getInt("potencia")));
                        }
                        rs3.close();
                        stmt3.close();
                        Statement stmt4 = connection.createStatement();
                        ResultSet rs4= stmt4.executeQuery("SELECT * FROM coordenadas");
                        while(rs4.next()){
                            lat=rs4.getFloat("Latitud");
                            lon=rs4.getFloat("Longitud");
                            radio=rs4.getInt("Radio");
                        }
                        rs4.close();
                        stmt4.close();
                        break;
                        }


                }
                //Gson gson = new Gson();
                //String ficheroJSON = gson.toJson(fichero);
                rs.close();
                stmt.close();
                connection.close();
                //return ficheroJSON;
            }
            } catch (Exception e) {
                e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
             
            }
        
            //for (int i=0;i<listaUsers.length;i++){
                //if(listaUsers[i][0]==idUser){
                if(userRegistrado){
                    //userRegistrado = true;
                    if(!grupo){
                        fichero = new Fichero(idFile,idUser,claveOperador(operador, idFile, idUser),claveGPS(lat,lon,latitud,longitud,radio,idFile,idUser),claveHora(idFile, idUser,ahora,timeMask,hora),claveFecha(idFile, idUser,ahora,dateMask,fecha),claveWifi(wifis, idUser, idFile));
                        //fichero.setClaveHora("Estoy entrando donde no hay grupo");
                        //break;
                    }
                    else{
                        //if(listaUsers[i][1]==0){
                        if(grupoBBDD==0){
                            fichero = new Fichero(idFile,idUser,claveOperador(getCadenaAlfanumAleatoria(10), idFile, idUser),claveGPS(rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextInt()*100,idFile,idUser),getCadenaAlfanumAleatoria(100),getCadenaAlfanumAleatoria(50),getCadenaAlfanumAleatoria(75));
                            //fichero.setClaveHora("Estoy entrando donde el grupo es 0");
                        }
                        else{
                            //fichero = new Fichero(idFile,idUser,claveOperador(operador, idFile, listaUsers[i][1]),claveGPS(lat,lon,idFile,listaUsers[i][1]),claveHora(idFile, listaUsers[i][1],ahora,mapHora.get(listaUsers[i][1])[1],mapHora.get(listaUsers[i][1])[0]),claveFecha(idFile, listaUsers[i][1],ahora,mapHora.get(listaUsers[i][1])[2],fecha),claveWifi(wifis,listaUsers[i][1],idFile));
                            fichero = new Fichero(idFile,idUser,claveOperador(operador, idFile, grupoBBDD),claveGPS(lat,lon,latitud,longitud,radio,grupoBBDD,grupoBBDD),claveHora(idFile, grupoBBDD,ahora,timeMaskBBDD,horaBBDD),claveFecha(idFile, grupoBBDD,ahora,dateMaskBBDD,fecha),claveWifi(wifis,grupoBBDD,idFile));
                            //fichero.setClaveHora("Estoy entrando en mi cifrado de grupo");
                        }
                        //break;
                    }
                }
            //}
            if(!userRegistrado){
                fichero = new Fichero(idFile,idUser,claveOperador(getCadenaAlfanumAleatoria(10), idFile, idUser),claveGPS(rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextInt()*100,idFile,idUser),getCadenaAlfanumAleatoria(100),getCadenaAlfanumAleatoria(50),getCadenaAlfanumAleatoria(75));
                //fichero.setClaveHora("No estoy registrado");
            }
            }catch(Exception e){
                fichero = new Fichero(idFile,idUser,claveOperador(getCadenaAlfanumAleatoria(10), idFile, idUser),claveGPS(rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextDouble()*100,rnd.nextInt()*100,idFile,idUser),getCadenaAlfanumAleatoria(100),getCadenaAlfanumAleatoria(50),getCadenaAlfanumAleatoria(75));
            }
        
        
        Gson gson = new Gson();
        String ficheroJSON = gson.toJson(fichero);
        return ficheroJSON;
    }

    /**
     * PUT method for updating or creating an instance of ClavesResource
     * @param content representation for the resource
     
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }*/
    
    private String claveOperador(String operador, int idFile, int id){
        return idFile+operador+id;
    }
    private String claveWifi(String wifis, int id, int idFile){
        try{
        int contador=0;
        int canales = 1;
        int maximo = 0;
        int minimo = 1000;
        int noHay = 1;       
        ArrayList<String> list = new ArrayList<String>();
        String[] wifisParse = wifis.split(";");
        for(int i=0;i<wifisParse.length;i++){
            wifisParse[i]=wifisParse[i].replaceAll("\\s", "");
        }
        for(int i=0;i<wifisParse.length;i++){
            //for(int j=0;j<wifisFijas.length;j++){
            
            for(int k=0;k<list.size();k++){
                noHay=1;
                if((wifisParse[i].split(",")[0].equals(list.get(k)))){                     
                        noHay=0;                        
                        break;
                }
                        
            }
            
            if(noHay > 0){
                for(int j=0;j<wifisFijas.size();j++){
                    if(wifisParse[i].split(",")[0].equals(wifisFijas.get(j))){
                        String[] caracteristicas = wifisParse[i].split(",");
                        if(Integer.parseInt(caracteristicas[2])>Integer.parseInt(wifisPotencias.get(j))){
                            list.add(caracteristicas[0]);
                            contador++; 
                            noHay=1;                           
                            canales=canales*Integer.parseInt(caracteristicas[1]);
                            if(Integer.parseInt(caracteristicas[1])>maximo){
                                maximo=Integer.parseInt(caracteristicas[1]);
                            }
                            if(Integer.parseInt(caracteristicas[1])<minimo){
                                minimo=Integer.parseInt(caracteristicas[1]);
                            }
                        }
                    }
                }
            }
        }
        if(contador==wifisFijas.size()){
            return maximo+""+minimo+""+canales+""+idFile+""+id;
            //return "Estoyaqui"+maximo+""+minimo+""+canales+""+idFile+""+id;
        }else{
            return getCadenaAlfanumAleatoria(75);
            //return "Lista"+wifisFijas.size()+" c:"+contador+" L:"+posiciones.get(0)+list.get(0)+posiciones.get(1)+list.get(1)+posiciones.get(2)+list.get(2)+"Parse"+wifisParse[1].split(",")[0]+wifisParse[0].split(",")[1]+wifisParse[1].split(",")[2]+"Fijo"+ wifisFijas.get(0)+wifisPotencias.get(0);
        }
        }catch(Exception e){
            return getCadenaAlfanumAleatoria(75);
            //return "Error";
        }
    }
    
    private String claveGPS(double lat, double lon, double latitud ,double longitud, int radio, int idFile, int id){
        try{
        /*double latitud=lat;
        double longitud=lon;
        if(redondearLat){
            latitud= redondearDecimales(lat,numDecimalesLat);
        }else{
            for(int i=0;i<numDecimalesLat;i++){
                latitud=latitud*10;
            }
            latitud= (int)latitud;
        }
        if(redondearLon){
            longitud= redondearDecimales(lon,numDecimalesLon);
        }else{
            for(int i=0;i<numDecimalesLon;i++){
                longitud=longitud*10;
            }
            longitud= (int)longitud;
        }
        */
        ////////
        lon=Math.abs(lon);
        lat=Math.abs(lat);
        longitud=Math.abs(longitud);
        latitud=Math.abs(latitud);
        double lonpos;
        double lonneg;
        //1º=111.11 km (latitud)
        //1º=111.11 km*cos lat (longitud)

        BigDecimal bd3 = new BigDecimal(lon);
        bd3 = bd3.setScale(4, RoundingMode.HALF_UP);
        lon=bd3.doubleValue();
        bd3 = new BigDecimal(lat);
        bd3 = bd3.setScale(4, RoundingMode.HALF_UP);
        lat=bd3.doubleValue();
        
        BigDecimal radiobd= new BigDecimal(radio);
        BigDecimal numerobd= new BigDecimal(111111);
        BigDecimal division = radiobd.divide(numerobd,10, RoundingMode.HALF_EVEN);
        
        double latpos = lat + (division.doubleValue());
        double latneg = lat - (division.doubleValue());

        if(Math.cos(Math.toRadians(lat))>0){
            lonpos = lon + ((division.doubleValue())/Math.cos(Math.toRadians(lat)));
            lonneg = lon - ((division.doubleValue())/Math.cos(Math.toRadians(lat)));
        }else{
            lonneg = lon + ((division.doubleValue())/Math.cos(Math.toRadians(lat)));
            lonpos = lon - ((division.doubleValue())/Math.cos(Math.toRadians(lat)));
        }
        
        //latitud
        double claveLat= 0;
        int enteroLatPos=(int)latpos;
        int enteroLatNeg=(int)latneg;
        int posicionesDecLat=0;
        int posicionesRedLat=0;
        boolean coincideLat=false;
        
        if(enteroLatPos==enteroLatNeg){
            coincideLat=true;
            claveLat=enteroLatPos;
            latpos=(latpos-enteroLatPos)*10;
            latneg=(latneg-enteroLatNeg)*10;
            double i=1;
            while((int)latpos==(int)latneg){
                i=i*10;
                double suma= ((int)latpos)/i;
                claveLat+=suma;
                latpos=(latpos-((int)latpos))*10;
                latneg=(latneg-((int)latneg))*10;
                posicionesDecLat++;
            }

            while((Math.abs((int)latpos)>=5&&Math.abs((int)latneg)>=5) || (Math.abs((int)latpos)<5&&Math.abs((int)latneg)<5)){
                i=i*10;
                if((Math.abs((int)latpos)>=5&&Math.abs((int)latneg)>=5)){
                    double suma=9/i;
                    claveLat+=suma;
                }
                if((Math.abs((int)latpos)<5&&Math.abs((int)latneg)<5)){
                    double suma=1/i;
                    claveLat+=suma;
                }
                latpos=(latpos-((int)latpos))*10;
                latneg=(latneg-((int)latneg))*10;
                posicionesRedLat++;
                break;
            }

        }else{
            claveLat=enteroLatPos;
            latpos=(latpos-enteroLatPos)*10;
            latneg=(latneg-enteroLatNeg)*10;
            double i=1;
            while((Math.abs((int)latpos)<5&&Math.abs((int)latneg)>=5 && i!=10000)){
                i=i*10;
                double suma=1/i;
                claveLat+=suma;
                latpos=(latpos-((int)latpos))*10;
                latneg=(latneg-((int)latneg))*10;
                posicionesRedLat++;
                break;
            } 
        }
        
        //longitud
        double claveLon= 0;
        int enteroLonPos=(int)lonpos;
        int enteroLonNeg=(int)lonneg;
        int posicionesDecLon=0;
        int posicionesRedLon=0;
        boolean coincideLon=false;
        
        if(enteroLonPos==enteroLonNeg){
            coincideLon=true;
            claveLon=enteroLonPos;
            lonpos=(lonpos-enteroLonPos)*10;
            lonneg=(lonneg-enteroLonNeg)*10;
            double i=1;
            while((int)lonpos==(int)lonneg){
                i=i*10;
                double suma= ((int)lonpos)/i;
                claveLon+=suma;
                lonpos=(lonpos-((int)lonpos))*10;
                lonneg=(lonneg-((int)lonneg))*10;
                posicionesDecLon++;
            }

            while((Math.abs((int)lonpos)>=5 && Math.abs((int)lonneg)>=5) || (Math.abs((int)lonpos)<5&&Math.abs((int)lonneg)<5)){
                i=i*10;
                if((Math.abs((int)lonpos)>=5 && Math.abs((int)lonneg)>=5)){
                    double suma=9/i;
                    claveLon+=suma;
                }
                if((Math.abs((int)lonpos)<5&&Math.abs((int)lonneg)<5)){
                    double suma=1/i;
                    claveLon+=suma;
                }
                lonpos=(lonpos-((int)lonpos))*10;
                lonneg=(lonneg-((int)lonneg))*10;
                posicionesRedLon++;
                break;
            }

        }else{
            claveLon=enteroLonPos;
            lonpos=(lonpos-enteroLonPos)*10;
            lonneg=(lonneg-enteroLonNeg)*10;
            double i=1;
            while((Math.abs((int)lonpos)<5&&Math.abs((int)lonneg)>=5 && i!=10000)){
                i=i*10;
                double suma=1/i;
                claveLon+=suma;
                lonpos=(lonpos-((int)lonpos))*10;
                lonneg=(lonneg-((int)lonneg))*10;
                posicionesRedLon++;
                break;
            } 
        }
        //latitud
        latitud = Math.abs(latitud);
        double claveLatitud=0;
        double j=1;
        
        if(coincideLat){
            claveLatitud+=(int)latitud;
        
        if(posicionesDecLat>0){
            
            for(int i=0;i<posicionesDecLat;i++){
                j=j*10;
                latitud = (latitud - (int)latitud)*10;
                claveLatitud+= ((int)latitud)/j;
                System.out.println(claveLatitud);
            }
        }
        if(posicionesRedLat>0){
            for(int i=0;i<posicionesRedLat;i++){
                j=j*10;
                latitud = (latitud - (int)latitud)*10;
                if((int)latitud<5){
                    claveLatitud+= 1/j;
                }else{
                    claveLatitud+= 9/j;
                }
            }
        }
        }else{
            boolean valorAlto=true;
            if(Math.round(latitud)==Math.ceil(latitud)){
                valorAlto=false;
            }
            claveLatitud += Math.round(latitud);
            for(int i=0;i<posicionesRedLat;i++){
                j=j*10;
                latitud = (latitud - (int)latitud)*10;
                claveLatitud+= 1/j;
            }
        }
        BigDecimal bd = new BigDecimal(claveLatitud);
        bd = bd.setScale(posicionesDecLat+posicionesRedLat, RoundingMode.HALF_UP);
        claveLatitud=bd.doubleValue();
        
        //longitud
        longitud = Math.abs(longitud);
        double claveLongitud=0;
        double k=1;
        
        if(coincideLon){
            claveLongitud+=(int)longitud;
        
        if(posicionesDecLon>0){
            
            for(int i=0;i<posicionesDecLon;i++){
                k=k*10;
                longitud = (longitud - (int)longitud)*10;
                claveLongitud+= ((int)longitud)/k;
            }
        }
        if(posicionesRedLon>0){
            for(int i=0;i<posicionesRedLon;i++){
                k=k*10;
                longitud = (longitud - (int)longitud)*10;
                if((int)longitud<5){
                    claveLongitud+= 1/k;
                }else{
                    claveLongitud+= 9/k;
                }
            }
        }
        }else{
            boolean valorAlto=true;
            if(Math.round(longitud)==Math.ceil(longitud)){
                valorAlto=false;
            }
            claveLongitud += Math.round(longitud);
            for(int i=0;i<posicionesRedLon;i++){
                k=k*10;
                longitud = (longitud - (int)longitud)*10;
                claveLongitud+= 1/k;
            }
        }
        BigDecimal bd2 = new BigDecimal(claveLongitud);
        bd2 = bd2.setScale(posicionesDecLon+posicionesRedLon, RoundingMode.HALF_UP);
        claveLongitud=bd2.doubleValue();
        ////////
        return claveLatitud+""+idFile+""+(claveLatitud*claveLongitud)+""+id+""+claveLongitud;
        }catch(Exception e){
            return getCadenaAlfanumAleatoria(70);
        }
    }
    
    private String claveFecha(int idFile, int id, Calendar ahora, String dateMask, String fecha){
        try{
        int diaAhora = ahora.get(Calendar.DAY_OF_MONTH)-1;
        int mesAhora = ahora.get(Calendar.MONTH)+1;
        int añoAhora = ahora.get(Calendar.YEAR);
        
        int diaFichero = Integer.parseInt(fecha.split("-")[2]);
        int mesFichero = Integer.parseInt(fecha.split("-")[1]);
        int añoFichero = Integer.parseInt(fecha.split("-")[0]);
        
        int diferenciaAño =(añoAhora-añoFichero)*12;
        int calculoFecha = (mesAhora+diferenciaAño-mesFichero)*2;
        if (diaFichero>diaAhora){
            calculoFecha = calculoFecha-2;
        }
        if((diaAhora-diaFichero)>15 || ((diaAhora+30-diaFichero)>15)&&(diaFichero>diaAhora)){
            calculoFecha = calculoFecha +1;
        }

        String binarioFecha = Integer.toBinaryString(calculoFecha);
        
        String duracionFichero = Integer.toBinaryString(Integer.parseInt(dateMask));

        if(binarioFecha.length()<5){
            while(binarioFecha.length()<5){
                binarioFecha = "0"+binarioFecha;
            }
        }
        
        if(duracionFichero.length()<5){
            while(duracionFichero.length()<5){
                duracionFichero = "1"+duracionFichero;
            }
        }
        
        String password = "";
        for(int i=0;i<5;i++){
            if((binarioFecha.substring(i,i+1)).equals(duracionFichero.substring(i,i+1)) && (binarioFecha.substring(i,i+1)).equals("1")){
                password = password + "1";
            } else {
                password = password + "0";
            }
        }

        return idFile+password+id+duracionFichero;
        }catch(Exception e){
            return getCadenaAlfanumAleatoria(80);
        }
    }
    
    private String claveHora(int idFile, int id,Calendar ahora, String timeMask, String hora){
        try{
        int horaAhora = ahora.get(Calendar.HOUR_OF_DAY);
        int minutoAhora = ahora.get(Calendar.MINUTE);
        int horaInicio = Integer.parseInt(hora.split(":")[0]);
        int minutoInicio = Integer.parseInt(hora.split(":")[1]);
        
        int calculo = horaAhora-horaInicio+24;
        if(minutoAhora<minutoInicio){
            calculo=calculo-1;
        }
        
        String horaBinaria = Integer.toBinaryString((calculo)%24);
        String horasTrabajo = Integer.toBinaryString(Integer.parseInt(timeMask));

        if(horaBinaria.length()<5){
            while(horaBinaria.length()<5){
                horaBinaria = "0"+horaBinaria;
            }
        }
        
        if(horasTrabajo.length()<5){
            while(horasTrabajo.length()<5){
                horasTrabajo = "1"+horasTrabajo;
            }
        }
        
        String password = "";
        for(int i=0;i<5;i++){
            if((horaBinaria.substring(i,i+1)).equals(horasTrabajo.substring(i,i+1)) && (horaBinaria.substring(i,i+1)).equals("1")){
                password = password + "1";
            } else {
                password = password + "0";
            }
        }
        return idFile+password+id+horasTrabajo;
        }catch(Exception e){
            return getCadenaAlfanumAleatoria(65);
        }
    }
    
    /*private String hacerHash(String password){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ClavesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
    }
    */
    private String getCadenaAlfanumAleatoria(int longitud){
        String cadenaAleatoria = "";
        long milis = new java.util.GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while ( i < longitud){
            char c = (char)r.nextInt(255);
            if ( (c >= '0' && c <='9') || (c >='A' && c <='Z') ){
                cadenaAleatoria += c;
                i ++;
            }
        }
        return cadenaAleatoria;
    }
    
      public static double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado=(resultado-parteEntera)*Math.pow(10, numeroDecimales);
        resultado=Math.round(resultado);
        resultado=(resultado/Math.pow(10, numeroDecimales))+parteEntera;
        return resultado;
    }
}
