package com.example.javafx;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;
/**
 * @author Roman Božik.
 * Hlavná trieda z ktorej sa celá hra spúšťa
 */
public class Main extends Application {
    static Clip clip;  /** Tu bude uložená hlavná zvučka. */
    static Clip zvuk;  /** Tu budú uložené postranné zvuky. */
    public static Pane MainPane;  /** Hlavný Pane. */
    public static Scene scene;  /** Hlavná a jediná scéna. */
    public static Stage stage;  /** Hlavný a jediný stejdž */
    public static int velkost = 50;  /** Tu sa určuje veľkosť postavy. */
    public static AnimationTimer funguj;  /** Hlavná "animácia", kde sa kontrolujú stlačené klávesy a fungovanie nepriateľa. */
    public static int velkost_postavy = 80;  /** Toto určuje veľkosť postavy. */
    public static Rectangle hp_bar = new Rectangle();  /** HP bar. */
    public static int aktualna_uroven = 0;  /** Aktuálny level. */

    private static HashMap<String, Boolean> currentlyActiveKeys = new HashMap<>();
    static Postava hrdina;  /** Tu je uložená trieda hrdinu. */
    public static ArrayList<Objekty> objekty = new ArrayList<>();  /** Tu sú uložené všetky objekty. *///Collections.synchronizedList(new ArrayList<>());
    @Override
    public void start(Stage stage) throws IOException {  /** Tu to štartuje. */
        try {
        clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File("src/main/resources/intro.wav")));
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gain.setValue(-25.0f); // Redukujem hlasitosť o X decibelov
        clip.start();
    }
        catch (Exception e)
    {
        e.printStackTrace(System.out);
    }
        Main.stage = stage;
        MainPane = new MainPane();
        scene = new Scene( MainPane,800, 600);
        stage.setTitle("Proj22-J4 Multicharakter");
        scene.getStylesheets().add("main.css");
        MainPane.setId("Pain");
        stage.setScene(scene);
        stage.show();
    }
    class MainPane extends Pane {  /** Tu sa kuchtí hlavné menu volaním Meníčka. */
        MainPane(){
            var menicko = new Menu(new Polozka[]{new Polozka("Nová hra"),new Polozka("Vybrať level"), new Polozka("Koniec")});
            menicko.setTranslateX(150);
            menicko.setTranslateY(125);
            getChildren().addAll(menicko,zaobleny_text());
        }
    }
    private static class Menu extends VBox {
        public Menu(Polozka[] ano) {
            for(var k : ano) getChildren().addAll(k);

        }



    }
    private static class Polozka extends StackPane {
        public Polozka(String textik) {
            Rectangle stvorec = new Rectangle(500,70);
            Text text = new Text(textik);
            LinearGradient mismas = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255))), new Stop(0.1, Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255))), new Stop(0.9, Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255))), new Stop(1, Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)))); //https://stackoverflow.com/questions/69841786/is-it-possible-to-make-a-percentage-of-a-linear-gradient-in-javafx
            stvorec.setOpacity(0.66);
            stvorec.setFill(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));
            text.setFill(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));
            text.setFont(Font.font(pickRandomFontFamily(), FontWeight.BOLD,30));
            setAlignment(Pos.CENTER);
            getChildren().addAll(stvorec, text);
            setOnMouseEntered(h -> {
                setOnMouseExited(e -> {
                    text.setFont(Font.font(pickRandomFontFamily(), FontWeight.BOLD,30));
                    stvorec.setFill(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));
                    text.setFill(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));
                });
                text.setFont(Font.font(pickRandomFontFamily(), FontWeight.BOLD,30));
                stvorec.setFill(mismas);
                text.setFill(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));

            });
            setOnMousePressed(l-> {
                stvorec.setFill(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));
                text.setFont(Font.font(pickRandomFontFamily(), FontWeight.BOLD,30));
                if(Objects.equals(textik, "Nová hra")){ aktualna_uroven=0;inithra();}
                else if(Objects.equals(textik, "Vybrať level")){ vybrat_level();}
                else if(Objects.equals(textik, "1")){ aktualna_uroven=0;inithra();}
                else if(Objects.equals(textik, "2")){ aktualna_uroven=1;inithra();}
                else if(Objects.equals(textik, "Koniec")){ System.exit(0);}
                try {
                    zvuk = AudioSystem.getClip();
                    zvuk.open(AudioSystem.getAudioInputStream(new File("src/main/resources/klik_menu.wav")));
                    FloatControl gain = (FloatControl) zvuk.getControl(FloatControl.Type.MASTER_GAIN);
                    gain.setValue(6.0f); // Redukujem hlasitosť o X decibelov
                    zvuk.start();
                }
                catch (Exception e)
                {
                    e.printStackTrace(System.out);
                }
            });

        }

    }
    private static String pickRandomFontFamily() { //https://stackoverflow.com/questions/64490124/how-to-generate-a-random-font-in-java
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.ENGLISH);

        return availableFonts[new Random().nextInt(availableFonts.length)];
    }
    private static Group zaobleny_text(){ //https://stackoverflow.com/questions/38406903/placing-text-in-a-circle-in-javafx
        Group textGroup = new Group();
        String welcome = "TRAJN";
        double rotation = -50;

        double radius = 100d;

        for (char c : welcome.toCharArray()) {
            Font font = Font.font(pickRandomFontFamily(), FontWeight.BOLD,45);
            if (!Character.isWhitespace(c)) {
                Text text = new Text(Character.toString(c));
                text.setFont(font);
                text.setFill(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));

                Rotate rotationMatrix = new Rotate(rotation, 0, radius);
                text.getTransforms().add(rotationMatrix);

                textGroup.getChildren().add(text);
            }
            rotation += 22.5;
        }
        textGroup.setTranslateX(400);
        textGroup.setTranslateY(60);
        return textGroup;
    }
    public static void inithra(){  /** Tu sa inicializuje hra. */
        objekty.clear();
        MainPane = new Pane();
        scene =  new Scene( MainPane,800, 600);
        stage.hide();
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        Main.MainPane.setLayoutX(0);
        clip.stop();
        if(zvuk!=null)zvuk.stop();

        //Pridať loading screen dokým cykli neskončia
        //Pridať lávu naspodok
        int i =0;
        //Generovanie levelu
        for(var p: Svet.vykreslenie[aktualna_uroven]){
            int j =0;
            for(var pismenko: Svet.vykreslenie[aktualna_uroven][i].toCharArray()){
                if(pismenko=='S') new Objekty(Objekty.Block.CRATE, j * velkost, i * velkost);
                    else if(pismenko=='G') new Objekty(Objekty.Block.GRASS, j * velkost, i * velkost);
                else if(pismenko=='T') new Objekty(Objekty.Block.BRICK,j*velkost,i*velkost);
                else if(pismenko=='R') new Objekty(Objekty.Block.WOODEN_TRAP_DOOR,j*velkost,i*velkost);

                    else if(pismenko=='C')new Objekty(Objekty.Block.COBBLE,j * velkost, i * velkost);
                else if(pismenko=='L') new Objekty(Objekty.Block.CRYING_OBSIDIAN,j*velkost,i*velkost);
                    else if(pismenko=='P')new Objekty(Objekty.Block.STROM,j * velkost, i * velkost);

                    else if(pismenko=='I')new Objekty(Objekty.Block.INVISIBLE,j * velkost, i * velkost);
                    else if(pismenko=='H')new Objekty(Objekty.Block.HEAL,j * velkost, i * velkost);
                j = j+1;
            }
            i = i+1;
        }
            for(var nepriatel: Svet.nepriatelia[aktualna_uroven]){
                if(nepriatel.docasny){
                    nepriatel.hp = -99;
                    continue;}
                nepriatel.refresh();
                MainPane.getChildren().add(nepriatel);
            }
        //Pridanie hráča
        hrdina = new Postava(0);
        hrdina.updatePostava();
        hrdina.setTranslateX(10);
        hrdina.setTranslateY(500);
        var empty_bar = new Rectangle(100,50,400,50);
        empty_bar.setFill(Color.BLACK);
        hp_bar = new Rectangle(100,50,((400*hrdina.hp)/100),50);
        hp_bar.setFill(Color.RED);
        Main.MainPane.getChildren().addAll(empty_bar,hp_bar);
        hrdina.translateXProperty().addListener((nepotrebne,nepotrebne2,totok)->{if(totok.longValue()>800){Main.MainPane.setLayoutX(-  (totok.longValue()-800)  );empty_bar.setTranslateX(totok.longValue()-800);hp_bar.setTranslateX(   (totok.longValue()-800) );hp_bar.setWidth((400*hrdina.hp)/100);}}); //https://www.tabnine.com/code/java/methods/javafx.scene.layout.Pane/translateXProperty
        Main.MainPane.getChildren().add(hrdina);
        // Stlačené klávesy
        scene.setOnKeyPressed(event -> {
            String codeString = event.getCode().toString();
            if (!currentlyActiveKeys.containsKey(codeString)) {
                currentlyActiveKeys.put(codeString, true);
            }
        });
        scene.setOnKeyReleased(event ->
                currentlyActiveKeys.remove(event.getCode().toString())
        );
        funguj = new AnimationTimer() {
            int vypnuta_hra = 0;
            long lastUpdate = 0 ;
            long lastupdate2 = 0;
            long lastupdate3 = 0;
            @Override
            public void handle(long now) {
                if (vypnuta_hra != 1) {
                    if (now - lastUpdate >= 80_000_000) {
                        lastUpdate = now;
                        if ((removeActiveKey("LEFT") || (removeActiveKey("A"))) && hrdina.getTranslateX() > 10) {
                            //System.out.println("left");
                            hrdina.setScaleX(-1); //otocenie panacika
                            if(hrdina.postava==2 && hrdina.aura!=null){
                                hrdina.pohyb(-10);
                                return;
                            }
                            else if(hrdina.postava==2 && hrdina.aura==null){
                                hrdina.pohyb(-40);
                                return;
                            }
                            else if(hrdina.postava==1){
                                hrdina.pohyb(-50);
                                return;
                            }
                            hrdina.pohyb(-50);
                        }

                        if ((removeActiveKey("RIGHT") || (removeActiveKey("D")) && hrdina.getTranslateX() + velkost_postavy < Svet.vykreslenie[aktualna_uroven][0].length() * velkost - 10)) {
                            //System.out.println("right");
                            hrdina.setScaleX(1);
                            if(hrdina.postava==2 && hrdina.aura!=null){
                                hrdina.pohyb(10);
                                return;
                            }
                            else if(hrdina.postava==2 && hrdina.aura==null){
                                hrdina.pohyb(42);
                                return;
                            }
                            else if(hrdina.postava==1){
                                hrdina.pohyb(50);
                                return;
                            }
                            hrdina.pohyb(53);
                        }

                        if ((removeActiveKey("UP") || removeActiveKey("W"))) {
                            //System.out.println("up");
                            if(hrdina.postava==2 && hrdina.aura!=null&&hrdina.skok){
                                hrdina.skok = false;
                                hrdina.spád -= 20;
                                return;
                            }
                            if(hrdina.postava==2 && hrdina.aura==null&& hrdina.skok){
                                hrdina.skok = false;
                                hrdina.spád -= 23;
                                return;
                            }
                            if(hrdina.postava==1&& hrdina.skok){
                                hrdina.skok = false;
                                hrdina.spád -= 25;
                                return;
                            }

                            if (hrdina.skok) {
                                hrdina.skok = false;
                                hrdina.spád -= 29;
                                try {
                                    int cislo = new Random().nextInt(1,6);
                                    zvuk = AudioSystem.getClip();
                                    zvuk.open(AudioSystem.getAudioInputStream(new File("src/main/resources/skok_"+cislo+".wav")));
                                    FloatControl gain = (FloatControl) zvuk.getControl(FloatControl.Type.MASTER_GAIN);
                                    gain.setValue(6.0f); // Redukujem hlasitosť o X decibelov
                                    zvuk.start();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace(System.out);
                                }
                            }
                        }

                        if ((removeActiveKey("DOWN") || removeActiveKey("S"))) {
                            //System.out.println("down");
                        }
                        if ((removeActiveKey("P"))) {
                            //System.out.println("down");
                            if (hrdina.postava == 2) hrdina.postava = 0;
                            else hrdina.postava += 1;
                            hrdina.updatePostava();
                        }
                        if ((removeActiveKey("SHIFT"))) {
                            //System.out.println("down");
                            if(hrdina.postava==1 && hrdina.utok.getStatus()== Animation.Status.RUNNING){
                                hrdina.utok.stop();
                                var docas = new ArrayList<Objekty>();
                                for(var objekt : Main.objekty){
                                    if((objekt.typ == Objekty.Block.CRATE || objekt.typ == Objekty.Block.CRYING_OBSIDIAN) && objekt.ocarovany) {
                                        objekt.setOnMouseDragged(null);
                                        docas.add(objekt);
                                    }

                                }
                                for(var k : docas){
                                    MainPane.getChildren().remove(k);
                                    objekty.remove(k);
                                    var docasnicek = new Objekty(k.typ, (int) k.getTranslateX(), (int) k.getTranslateY());
                                    docasnicek.block.setEffect(k.block.getEffect());
                                    docasnicek.ocarovany = true;
                                    var plamienok = new ImageView(new Image("plamienok_cerveny.gif"));
                                    plamienok.setFitHeight(docasnicek.block.getFitHeight()*1.5);
                                    plamienok.setFitWidth(docasnicek.block.getFitWidth()*1.5);
                                    plamienok.setTranslateX(docasnicek.block.getTranslateX()-12);
                                    plamienok.setTranslateY(docasnicek.block.getTranslateY()-10);
                                    docasnicek.getChildren().add(plamienok);
                                    docasnicek.getChildren().remove(docasnicek.block);
                                    docasnicek.getChildren().add(docasnicek.block);
                                    if(new Random().nextBoolean()&&new Random().nextBoolean()){
                                        var nepriatel = new Nepriatel(Nepriatel.typ_nepriatela.NightBorne, (int) docasnicek.getTranslateX(), (int) docasnicek.getTranslateY());
                                        nepriatel.hp = new Random().nextInt(70);
                                        MainPane.getChildren().add(nepriatel);
                                        nepriatel.docasny = true;
                                        Svet.nepriatelia[aktualna_uroven] = Svet.AddToStringArray(Svet.nepriatelia[aktualna_uroven],nepriatel);
                                    }
                                }
                                if(clip!=null) clip.stop();
                                return;
                            }
                            else if(hrdina.postava==1){
                                try {
                                    clip = AudioSystem.getClip();
                                    clip.open(AudioSystem.getAudioInputStream(new File("src/main/resources/mag_schopnost_aktivacia.wav")));
                                    FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                                    gain.setValue(+5.0f); // Redukujem hlasitosť o X decibelov
                                    clip.start();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace(System.out);
                                }
                            } else if (hrdina.postava==2&& hrdina.aura!=null) {
                                hrdina.getChildren().remove(hrdina.aura);
                                hrdina.aura = null;
                                return;
                            }
                            if(hrdina.postava==0){
                                if(new Random().nextBoolean()){
                                    try {
                                        Main.zvuk = AudioSystem.getClip();
                                        zvuk.open(AudioSystem.getAudioInputStream(new File("src/main/resources/luk1.wav")));
                                        FloatControl gain = (FloatControl) zvuk.getControl(FloatControl.Type.MASTER_GAIN);
                                        gain.setValue(6.0f); // Redukujem hlasitosť o X decibelov
                                        zvuk.start();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace(System.out);
                                    }
                                }
                                else {
                                    try {
                                        zvuk = AudioSystem.getClip();
                                        zvuk.open(AudioSystem.getAudioInputStream(new File("src/main/resources/luk2.wav")));
                                        FloatControl gain = (FloatControl) zvuk.getControl(FloatControl.Type.MASTER_GAIN);
                                        gain.setValue(6.0f); // Redukujem hlasitosť o X decibelov
                                        zvuk.start();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace(System.out);
                                    }
                                }
                            }
                            hrdina.schopnost();
                        }
                        if ((removeActiveKey("SPACE"))) {
                            //System.out.println("space");
                            hrdina.utok_animacia();
                        }
                        //System.out.println(currentlyActiveKeys);
                        currentlyActiveKeys.clear();
                        hrdina.kontrola_heal();
                        if(hrdina.kontrola_konca()){
                            vypnuta_hra = 1;
                            Main.MainPane.getChildren().clear();
                            objekty.clear();
                            MainPane = new Pane();
                            scene = new Scene(MainPane, 800, 600);
                            stage.hide();
                            stage.setMaximized(true);
                            stage.setScene(scene);
                            stage.show();
                            Main.MainPane.setLayoutX(0);
                            //this.stop();
                            aktualna_uroven++;
                            if(aktualna_uroven==2){
                                    vyhra();
                            }
                            else {
                                inithra();
                                return;
                            }
                        }
                    }
                    if (now - lastupdate2 >= 22_000_000) {
                        lastupdate2 = now;
                        if (hrdina.spád < 10) {
                            hrdina.spád += 1;
                        }
                        for(var nepriatel: Svet.nepriatelia[aktualna_uroven]){
                            if(nepriatel.mrtvy)continue;
                            if(nepriatel.hp<1) {
                                try {
                                    int cislo = new Random().nextInt(1,6);
                                    zvuk = AudioSystem.getClip();
                                    zvuk.open(AudioSystem.getAudioInputStream(new File("src/main/resources/smrt_"+cislo+".wav")));
                                    FloatControl gain = (FloatControl) zvuk.getControl(FloatControl.Type.MASTER_GAIN);
                                    gain.setValue(6.0f); // Redukujem hlasitosť o X decibelov
                                    zvuk.start();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace(System.out);
                                }
                                nepriatel.mrtvy = true;
                                Main.MainPane.getChildren().remove(nepriatel);
                                continue;
                            }
                            if (nepriatel.spád < 10) {
                                nepriatel.spád += 1;
                            }
                            nepriatel.kontrola_vysky(nepriatel.spád);
                            nepriatel.hyb_sa();

                        }
                        hrdina.kontrola_vysky(hrdina.spád);
                        hrdina.kontrola_sipov(45);
                        hp_bar.setWidth((400*hrdina.hp)/100);
                    }
                    if ((hrdina.hp < 1 || hrdina.getTranslateY() > 900) && vypnuta_hra != 1) {
                        vypnuta_hra = 1;
                        Main.MainPane.getChildren().clear();
                        objekty.clear();
                        MainPane = new Pane();
                        scene = new Scene(MainPane, 800, 600);
                        stage.hide();
                        stage.setMaximized(true);
                        stage.setScene(scene);
                        stage.show();
                        zvuk.stop();
                        Main.MainPane.setLayoutX(0);
                        //this.stop();
                        inithra();
                        return;

                    }
                }
            }
        };
        funguj.start();


    }
    public static void vybrat_level(){  /** metóda na výber levelu. */
        Main.MainPane.getChildren().clear();
        var menicko = new Menu(new Polozka[]{new Polozka("1"),new Polozka("2")});
       menicko.setTranslateX(150);
        menicko.setTranslateY(125);
        Main.MainPane.getChildren().addAll(menicko,zaobleny_text());
    }
    public static void vyhra(){  /** Záverečná obrazovka. */
        Main.MainPane.getChildren().clear();
        objekty.clear();
        MainPane = new Pane();
        scene = new Scene(MainPane, 800, 600);
        stage.hide();
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        Main.MainPane.setLayoutX(0);
        scene.setFill(Color.BLACK);
        var vyhra = new ImageView(new Image("vyhra.jpg"));
        Main.MainPane.getChildren().add(vyhra);
        vyhra.setTranslateX(500);
        vyhra.setTranslateY(250);
        clip.stop();
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("src/main/resources/vyhra.wav")));
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(-25.0f); // Redukujem hlasitosť o X decibelov
            clip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
    }
    private static boolean removeActiveKey(String codeString) { //https://stackoverflow.com/questions/37472273/detect-single-key-press-in-javafx
        Boolean isActive = currentlyActiveKeys.get(codeString);

        if (isActive != null && isActive) {
            currentlyActiveKeys.put(codeString, false);
            return true;
        } else {
            return false;
        }
    }



    public static void main(String[] args) {  /** tu sa to štartuje ešte pred štartom :). */
        launch();
    }
}