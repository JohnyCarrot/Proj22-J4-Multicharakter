package com.example.javafx;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author Roman Božik.
 * Tu sa nachádzajú všetky potrebné metody pre správne fungovanie nepriatela
 */
public class Nepriatel extends Pane {  /** Konštruktor. */
    public enum typ_nepriatela {  /** Ten istý trik čo pri objektoch. */
        Skeleton, NightBorne
    }
    ImageView skin = new ImageView(new Image("skeleton_idle.png"));  /** Tu je uložený skin nepritaela. */
    public int spád = 0;  /** Gravitácia lepšie velocity. */
    boolean mrtvy = false;  /** je nažive ?. */
    public boolean skok = true;  /** Môže skočiť ?. */
    boolean docasny = false;  /** Je len na jedno použitie ?. */
    AnimationTimer chôdza;  /** Tu je anímácia chôdze. */

    int chôdza_snimok = 1;  /** Reprezentácia konkrétneho snímku. */
    Timeline utok = new Timeline();  /** Animácia útoku. */
    AtomicInteger frejm; /** Konkrétny frejm. *///Frame animácie útoku
    int hp = 100;  /** Aktuálne hpt. */
    int default_hp = 100;  /** defaultne hp. */
    int default_x;  /** defaultna pozicia X. */
    int default_y;  /** def. pozícia Y. */
    typ_nepriatela typ;  /** typ nepriatela. */
    public Nepriatel(typ_nepriatela typik,int x,int y){
        typ = typik;
        skin.setFitHeight(Main.velkost_postavy);
        skin.setFitWidth(Main.velkost_postavy);
        skin = new ImageView(new Image("skeleton_idle.png"));
        if(typ==typ_nepriatela.NightBorne) skin = new ImageView(new Image("NightBorne_idle.gif"));
        skin.setFitWidth(Main.velkost_postavy);
        skin.setFitHeight(Main.velkost_postavy);
        getChildren().add(skin);
        setTranslateX(x);
        setTranslateY(y);
        default_x = x;
        default_y = y;
        if(typ==typ_nepriatela.NightBorne){
            hp = 200;
            default_hp = 200;
        }
        chôdza = new AnimationTimer() {
            long lastUpdate = 0 ;
            @Override
            public void handle(long l) {
                //if (l - lastUpdate >= 250_000_000) {
                lastUpdate = l;
                getChildren().clear();
                try {
                    skin = new ImageView(new Image("skeleton_chodza (" + chôdza_snimok + ").png"));
                }
                catch (Exception e){
                    System.out.println("Chyba na skeleton chôdze");
                }

                skin.setFitHeight(Main.velkost_postavy);
                skin.setFitWidth(Main.velkost_postavy);
                getChildren().addAll(skin);
                //chôdza_snimok++;
                if (chôdza_snimok==10) chôdza_snimok =1;
                //}
            }
        };
        chôdza.stop();
        if(typ==typ_nepriatela.NightBorne){
            chôdza = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    if(!Objects.equals(skin.getId(), "chodza")){
                        getChildren().clear();
                        skin = new ImageView(new Image("NightBorne_chodza.gif"));
                        skin.setFitHeight(Main.velkost_postavy);
                        skin.setFitWidth(Main.velkost_postavy);
                        skin.setId("chodza");
                        getChildren().add(skin);
                        if (chôdza_snimok==10) chôdza_snimok =1;
                    }
                }
            };
        }
    }
    public void refresh(){  /** da nepriatela s5 do formy. */
        setTranslateX(default_x);
        setTranslateY(default_y);
        mrtvy = false;
        hp = default_hp;
    }
    public void kontrola_vysky(int cislo){  /** deto ako pri hráčovi. */
        //System.out.println(getTranslateY());
        boolean vl = true;

        if (cislo >0) vl=false;
        else {cislo*=-1;}    //https://gamedev.stackexchange.com/questions/132873/how-to-apply-jump-and-gravity-in-java
        for(int i= 0;i <cislo; i++){
            for(var objekt :Main.objekty){
                if(getBoundsInParent().intersects(objekt.getBoundsInParent())){
                    if(vl){
                        //Spodná časť obrázku + veľkosť postavy = spodná časť platformy
                        if( objekt.getTranslateY()+ Main.velkost==getTranslateY()){
                            setTranslateY(getTranslateY()+1);
                            spád = 12;
                            return;
                        }
                    }
                    else{
                        //Kontrola státia na platforme
                        if(Main.velkost_postavy+getTranslateY()== objekt.getTranslateY()){skok = true;
                            setTranslateY(getTranslateY()-1);
                            return;
                        }
                    }
                }
            }
            if(vl)setTranslateY(-1+getTranslateY());
            else setTranslateY(+1+getTranslateY());
            //System.out.println(getTranslateY());
            // Kontrola spadnutia do lavy,pridat death screen


        }
    }
    public void hyb_sa(){  /** Niekto / Niečo toho nepriatela ovládať musí */
        if(getTranslateY()>Main.hrdina.getTranslateY()&&new Random().nextBoolean()&&new Random().nextBoolean()){
            if (skok) {
                skok = false;
                spád -= 25;
                //pridaj hudbu skoku
                return;
            }
        }
        if(typ==typ_nepriatela.NightBorne){
            if (getTranslateX() - Main.hrdina.getTranslateX() < 1200 && getTranslateX() - Main.hrdina.getTranslateX() > 0) {
                setScaleX(-1);
                if (getBoundsInParent().intersects(Main.hrdina.getBoundsInParent())) {
                    chôdza.stop();
                    utok_animacia();
                }
                else if (new Random().nextBoolean() && utok != null && utok.getStatus() != Animation.Status.RUNNING) {
                    if (new Random().nextBoolean()) {
                        pohyb(-new Random().nextInt(25));
                    }
                }
            } else if (getTranslateX() - Main.hrdina.getTranslateX() < 0 && getTranslateX() - Main.hrdina.getTranslateX() > -2200) {
                setScaleX(1);
                if ((getBoundsInParent().intersects(Main.hrdina.getBoundsInParent())))
                    utok_animacia();
                pohyb(new Random().nextInt(18));
            }
        }
        else {
            if (getTranslateX() - Main.hrdina.getTranslateX() < 1200 && getTranslateX() - Main.hrdina.getTranslateX() > 0) {
                setScaleX(1);
                if (new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean())
                    utok_animacia();
                else if (new Random().nextBoolean() && utok != null && utok.getStatus() != Animation.Status.RUNNING) {
                    if (new Random().nextBoolean()) {
                        pohyb(-new Random().nextInt(10));
                    }
                }

            } else if (getTranslateX() - Main.hrdina.getTranslateX() < 0 && getTranslateX() - Main.hrdina.getTranslateX() > -2200) {
                setScaleX(-1);
                if (new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean())
                    utok_animacia();
                pohyb(new Random().nextInt(14));
            }
        }
    }
    public void pohyb(int cislo){  /** Pohyb dopredu / dozadu. */
        if(utok!=null){
            if(utok.getStatus()== Animation.Status.RUNNING) return;
        }
        if(typ==typ_nepriatela.NightBorne){
            chôdza.stop();
        }
        chôdza.start();
        boolean vl = true;
        if (cislo >0) vl=false;
        else {cislo*=-1;}
        for(int i = 0; i<cislo; i++) {
            for (var objekt : Main.objekty) { //https://stackoverflow.com/questions/39717053/algorithm-to-verify-if-a-line-is-intersect-another-javafx
                if(this.getBoundsInParent().intersects(objekt.getBoundsInParent())) {
                    if (vl) {if (getTranslateX()== Main.velkost+objekt.getTranslateX()) return;;
                    } else if (getTranslateX()+ Main.velkost_postavy== objekt.getTranslateX())return;

                }
            }
            if(vl)setTranslateX(-1+getTranslateX());
            else setTranslateX(+1+getTranslateX());
        }
        chôdza_snimok++;
    }
    public void utok_animacia() {  /** Tu s tvorí zlo vo forme útoku. */
        if (utok != null) {
            if (utok.getStatus() == Animation.Status.RUNNING) return;
        }

        chôdza.stop();
        if (typ == typ_nepriatela.NightBorne) {

            utok = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                if (!Objects.equals(skin.getId(), "utok")) {
                    chôdza.stop();
                    getChildren().clear();
                    skin = new ImageView(new Image("NightBorne_utok.gif"));
                    skin.setId("utok");
                    skin.setFitHeight(Main.velkost_postavy);
                    skin.setFitWidth(Main.velkost_postavy);
                    getChildren().addAll(skin);
                    //System.out.println("Hram");
                }
                if(getBoundsInParent().intersects(Main.hrdina.getBoundsInParent())){
                    if(Main.hrdina.postava == 2) Main.hrdina.hp = Main.hrdina.hp - 10;
                    else  Main.hrdina.hp = Main.hrdina.hp - 20;
                }
            }));
            utok.setAutoReverse(true);
            utok.setCycleCount(1);
            utok.play();

        } else {
            frejm = new AtomicInteger(1);
            utok = new Timeline(
                    new KeyFrame(Duration.seconds(0.05), event -> {
                        getChildren().clear();
                        skin = new ImageView(new Image("skeleton_utok (" + frejm + ").png"));
                        //Mág nemôže útočiť //else if(postava==1)skin = new ImageView(new Image("strelec_utok (" + frejm + ").png"));
                        skin.setFitHeight(Main.velkost_postavy);
                        skin.setFitWidth(Main.velkost_postavy);
                        frejm.getAndIncrement();
                        if (frejm.get() == 11) frejm.set(1);
                        if (frejm.get() == 10) {
                            var sip = new Objekty(Objekty.Block.ARROW, (int) Math.round(getTranslateX()) + 10, (int) Math.round(getTranslateY()) + 32);
                            if (getScaleX() > 0) sip.smer_vpravo = false;
                            sip.objekt_patri_nepriatelovi = true;
                        }
                        getChildren().addAll(skin);

                    })
            );
            utok.setAutoReverse(true);
            utok.setCycleCount(20);
            utok.play();
        }
    }
}
