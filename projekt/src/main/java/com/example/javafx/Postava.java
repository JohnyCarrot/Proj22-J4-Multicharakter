package com.example.javafx;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author Roman Božik.
 * Tu sú uložené vlastnosti postavy
 */

public class Postava extends Pane{
    ImageView skin = new ImageView(new Image("strelec_idle.png"));  /** Skin postavy. */
    ImageView aura= null;  /**Aktívna aura. */
    public int spád = 0;  /** Tu je uložené gravitácia, resp, lepšie skôr zrýchlenie. */
    int strelec_hp = 100;  /** default hp strelca. */
    int mag_hp = 100;  /** default hp maga. */
    int war_hp = 100;  /** default hp wara */
    int hp = 100;  /** práve aktívne hp. */
    Timeline utok;  /** Tu je uložená animácia utoku. */
    AtomicInteger frejm; /** Tu je uložený aktuálny frejm útoku . *///Frame animácie útoku
    public boolean skok = true;  /** Kontrola, či môžem skočiť */

    int postava;  /** Číslo postavy. */
    AnimationTimer chôdza;  /** Animácia chôdze. */

    int chôdza_snimok = 1;  /** Konkrétny snímok chôdze. */

    public Postava(int postavicka){
        postava=postavicka;
        skin.setFitHeight(Main.velkost_postavy);
        skin.setFitWidth(Main.velkost_postavy);
    }
    public void utok_animacia() {  /** Animácia útoku. */
        if(utok!=null){
            if(utok.getStatus()== Animation.Status.RUNNING) return;
        }
        chôdza.stop();
        frejm = new AtomicInteger(1);
        utok = new Timeline(
                new KeyFrame(Duration.seconds(0.05), event -> {
                    getChildren().clear();
                    if(postava==0)skin = new ImageView(new Image("strelec_utok (" + frejm + ").png"));
                    else if(postava==1)skin = new ImageView(new Image("mag_utok (" + frejm + ").png"));
                    else if(postava==2)skin = new ImageView(new Image("war_utok (" + frejm + ").png"));
                    skin.setFitHeight(Main.velkost_postavy);
                    skin.setFitWidth(Main.velkost_postavy);
                    frejm.getAndIncrement();
                    if(frejm.get()== 11)frejm.set(1);
                    if(frejm.get()==7 && postava==0) {
                        var sip = new Objekty(Objekty.Block.ARROW, (int) Math.round(getTranslateX()) + 10, (int) Math.round(getTranslateY()) + 32);
                        if (getScaleX() < 0) sip.smer_vpravo = false;
                        ColorAdjust colorAdjust = new ColorAdjust();
                        colorAdjust.setContrast(0);
                        colorAdjust.setHue(0);
                        colorAdjust.setBrightness(0);
                        colorAdjust.setSaturation(0);
                        sip.block.setEffect(colorAdjust);
                    }
                    if(frejm.get()==7&&postava==1){

                        if(Main.hrdina.getTranslateY()>560) {
                            try {
                                Main.zvuk = AudioSystem.getClip();
                                int cislo = new Random().nextInt(1,4);
                                Main.zvuk.open(AudioSystem.getAudioInputStream(new File("src/main/resources/mag_hulka_"+cislo+".wav")));
                                FloatControl gain = (FloatControl) Main.zvuk.getControl(FloatControl.Type.MASTER_GAIN);
                                gain.setValue(6.0f); // Redukujem hlasitosť o X decibelov
                                Main.zvuk.start();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace(System.out);
                            }
                            var skatula = new Objekty(Objekty.Block.CRATE, (int) Math.round(getTranslateX()) + 10, (int) Math.round(getTranslateY()));
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setContrast(0);
                            colorAdjust.setHue(0);
                            colorAdjust.setBrightness(0);
                            colorAdjust.setSaturation(0);
                            skatula.block.setEffect(colorAdjust);
                            var plamienok = new ImageView(new Image("plamienok.gif"));
                            plamienok.setFitHeight(skatula.block.getFitHeight()*1.5);
                            plamienok.setFitWidth(skatula.block.getFitWidth()*1.5);
                            plamienok.setTranslateX(skatula.block.getTranslateX()-12);
                            plamienok.setTranslateY(skatula.block.getTranslateY()-10);
                            skatula.getChildren().add(plamienok);
                            skatula.getChildren().remove(skatula.block);
                            skatula.getChildren().add(skatula.block);
                        }
                    }
                    if(frejm.get()==7&&postava==2){
                        for(var nepriatel: Svet.nepriatelia[Main.aktualna_uroven]){
                            if(nepriatel.hp<1) continue;
                            if(getBoundsInParent().intersects(nepriatel.getBoundsInParent())){
                                try {
                                    var zvuk = AudioSystem.getClip();
                                    int cislo = new Random().nextInt(1,8);
                                    zvuk.open(AudioSystem.getAudioInputStream(new File("src/main/resources/mec_"+cislo+".wav")));
                                    FloatControl gain = (FloatControl) zvuk.getControl(FloatControl.Type.MASTER_GAIN);
                                    gain.setValue(6.0f); // Redukujem hlasitosť o X decibelov
                                    zvuk.start();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace(System.out);
                                }
                                if(aura==null) nepriatel.hp = nepriatel.hp-50;
                                else nepriatel.hp = nepriatel.hp-20;

                            }

                        }
                        var na_odstranenie = new ArrayList<Objekty>();
                        for (var skatula: Main.objekty){
                            if(skatula.typ== Objekty.Block.CRATE){
                                if(getBoundsInParent().intersects(skatula.getBoundsInParent())){
                                    Main.MainPane.getChildren().remove(skatula);

                                    na_odstranenie.add(skatula);

                                }
                            }
                        }
                        for(var k: na_odstranenie)Main.objekty.remove(k);
                    }
                    if(aura!=null && postava==2) getChildren().addAll(aura);
                    getChildren().addAll(skin);

                })
        );
        utok.setAutoReverse(true);
        utok.setCycleCount(20);
        utok.play();
    }
    public void schopnost(){  /** Tu sa kuchtí schopnosť aj s efektami. */
        if(postava==0) {
            if (utok != null) {
                if (utok.getStatus() == Animation.Status.RUNNING) return;
            }
            chôdza.stop();
            frejm = new AtomicInteger(1);
            utok = new Timeline(
                    new KeyFrame(Duration.seconds(0.05), event -> {
                        getChildren().clear();
                        skin = new ImageView(new Image("strelec_utok (" + frejm + ").png"));
                        skin.setFitHeight(Main.velkost_postavy);
                        skin.setFitWidth(Main.velkost_postavy);
                        frejm.getAndIncrement();
                        if (frejm.get() == 11) frejm.set(1);
                        if (frejm.get() == 7 && postava == 0) {
                            var sip = new Objekty(Objekty.Block.ARROW, (int) Math.round(getTranslateX()) + 20, (int) Math.round(getTranslateY()));
                            if (getScaleX() < 0) sip.smer_vpravo = false;
                            ColorAdjust colorAdjust = new ColorAdjust();
                            colorAdjust.setContrast(0.5);
                            colorAdjust.setHue(-0.5);
                            colorAdjust.setBrightness(0.5);
                            colorAdjust.setSaturation(-0.5);
                            sip.block.setEffect(colorAdjust);
                            sip.special = true;
                            sip.block.setFitWidth(sip.block.getFitWidth()*2);
                            sip.block.setFitHeight(sip.block.getFitHeight()*2);
                        }
                        getChildren().addAll(skin);

                    })
            );
            utok.setAutoReverse(true);
            utok.setCycleCount(20);
            utok.play();
        } else if (postava==1) {
            if (utok != null) {
                if (utok.getStatus() == Animation.Status.RUNNING) return;
            }
            chôdza.stop();
            AtomicInteger cas = new AtomicInteger();
            frejm = new AtomicInteger(1);
            utok = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), event -> { //ma dlzke nezalezi, je to len jeden obrazok
                        getChildren().clear();
                        skin = new ImageView(new Image("mag_utok_special.png"));
                        skin.setFitHeight(Main.velkost_postavy);
                        skin.setFitWidth(Main.velkost_postavy);
                        getChildren().addAll(skin);
                        hp = hp -5;
                        cas.getAndIncrement();
                        if(cas.get()==1){
                            try {
                                Main.clip = AudioSystem.getClip();
                                Main.clip.open(AudioSystem.getAudioInputStream(new File("src/main/resources/mag_schopnost_beats.wav")));
                                FloatControl gain = (FloatControl) Main.clip.getControl(FloatControl.Type.MASTER_GAIN);
                                gain.setValue(+6.0f); // Redukujem hlasitosť o X decibelov
                                Main.clip.start();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace(System.out);
                            }
                        }


                    })
            );

            for(var objekt: Main.objekty){
                if(objekt.typ!= Objekty.Block.CRATE && objekt.typ!= Objekty.Block.CRYING_OBSIDIAN) continue;
                //objekt.setOnMousePressed(null);
                objekt.setOnMouseDragged(e -> {
                    objekt.setTranslateX(objekt.getTranslateX()+e.getX());
                    objekt.setTranslateY(objekt.getTranslateY()+e.getY());
                    if(objekt.ocarovany==false) {
                        var plamienok = new ImageView(new Image("plamienok_cerveny.gif"));
                        plamienok.setFitHeight(objekt.block.getFitHeight() * 1.5);
                        plamienok.setFitWidth(objekt.block.getFitWidth() * 1.5);
                        plamienok.setTranslateX(objekt.block.getTranslateX() - 12);
                        plamienok.setTranslateY(objekt.block.getTranslateY() - 10);
                        objekt.getChildren().add(plamienok);
                        objekt.getChildren().remove(objekt.block);
                        objekt.getChildren().add(objekt.block);
                        objekt.ocarovany = true;
                    }
                    //System.out.println(e.getSceneY());

                });

                //objekt.setOnMouseDragged(null);
            }
            utok.setAutoReverse(true);
            utok.setCycleCount(Animation.INDEFINITE);
            utok.play();

        } else if (postava ==2) {
            try {
                Main.zvuk = AudioSystem.getClip();
                Main.zvuk.open(AudioSystem.getAudioInputStream(new File("src/main/resources/zapnutie_aury.wav")));
                FloatControl gain = (FloatControl) Main.zvuk.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(6.0f); // Redukujem hlasitosť o X decibelov
                Main.zvuk.start();
            }
            catch (Exception e)
            {
                e.printStackTrace(System.out);
            }
            aura = new ImageView(new Image("aura2.gif"));
            aura.setFitHeight(Main.velkost * 1.5);
            aura.setFitWidth(Main.velkost  * 2.4);
            aura.setTranslateX(aura.getTranslateX() - 20);
            aura.setTranslateY(aura.getTranslateY() - 15);
            getChildren().addAll(aura);
            Main.MainPane.getChildren().remove(this);
            Main.MainPane.getChildren().add(this);
        }

    }

    public void pohyb(int cislo){  /** Určuje pohyb dopredu / dozadu. */
        if(utok!=null){
            if(utok.getStatus()== Animation.Status.RUNNING) return;
        }
        chôdza.start();
        boolean vl = true;
        if (cislo >0) vl=false;
        else {cislo*=-1;}
        for(int i = 0; i<cislo; i++) {
            for (var objekt : Main.objekty) { //https://stackoverflow.com/questions/39717053/algorithm-to-verify-if-a-line-is-intersect-another-javafx
                if(this.getBoundsInParent().intersects(objekt.getBoundsInParent())) {
                    if (vl) {if (getTranslateX()== Main.velkost+objekt.getTranslateX()) {setTranslateX(getTranslateX()+1);} //Kontrola rohov
                    } else if (Main.velkost_postavy+getTranslateX()== objekt.getTranslateX()){setTranslateX(getTranslateX()-1);return;}

                }
            }
            if(vl)setTranslateX(-1+getTranslateX());
            else setTranslateX(+1+getTranslateX());
        }
        chôdza_snimok++;
    }
    public boolean kontrola_konca(){  /** Kontroluje, či hráč dobehol CCA na koniec mapy. */
        if(Main.velkost *Svet.vykreslenie[Main.aktualna_uroven][0].length() - 500<getTranslateX()){
            return true;
        }
        return false;
    }


    public void updatePostava() {  /** Zmena postavy */
        aura =null;
        if(chôdza!=null) chôdza.stop();
        getChildren().clear();
        if (postava==0){
            war_hp = hp;
            hp = strelec_hp;
            getChildren().clear();
            if(chôdza!=null) chôdza.stop();
            skin = new ImageView(new Image("strelec_idle.png"));
            skin.setFitHeight(Main.velkost_postavy);
            skin.setFitWidth(Main.velkost_postavy);
            getChildren().addAll(skin);
            chôdza = new AnimationTimer() {
                long lastUpdate = 0 ;
                @Override
                public void handle(long l) {
                    //if (l - lastUpdate >= 250_000_000) {
                        lastUpdate = l;
                        getChildren().clear();
                        skin = new ImageView(new Image("strelec_chodza (" + chôdza_snimok + ").png"));
                        skin.setFitHeight(Main.velkost_postavy);
                        skin.setFitWidth(Main.velkost_postavy);
                        getChildren().addAll(skin);
                        //chôdza_snimok++;
                        if (chôdza_snimok==10) chôdza_snimok =1;
                    //}
                }
            };



        }
        if (postava==1){
            strelec_hp = hp;
            hp = mag_hp;
            getChildren().clear();
            if(chôdza!=null) chôdza.stop();
            skin = new ImageView(new Image("mag_idle.png"));
            skin.setFitHeight(Main.velkost_postavy);
            skin.setFitWidth(Main.velkost_postavy);
            getChildren().addAll(skin);
            chôdza = new AnimationTimer() {
                long lastUpdate = 0 ;
                @Override
                public void handle(long l) {
                    //if (l - lastUpdate >= 250_000_000) {
                    lastUpdate = l;
                    getChildren().clear();
                    skin = new ImageView(new Image("mag_chodza (" + chôdza_snimok + ").png"));
                    skin.setFitHeight(Main.velkost_postavy);
                    skin.setFitWidth(Main.velkost_postavy);
                    getChildren().addAll(skin);
                    //chôdza_snimok++;
                    if (chôdza_snimok==10) chôdza_snimok =1;
                    //}
                }
            };
        }
        if (postava==2){
            mag_hp = hp;
            hp = war_hp;
            getChildren().clear();
            if(chôdza!=null) chôdza.stop();
            skin = new ImageView(new Image("war_idle.png"));
            skin.setFitHeight(Main.velkost_postavy);
            skin.setFitWidth(Main.velkost_postavy);
            getChildren().addAll(skin);
            chôdza = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    getChildren().clear();
                    skin = new ImageView(new Image("war_chodza (" + chôdza_snimok + ").png"));
                    skin.setFitHeight(Main.velkost_postavy);
                    skin.setFitWidth(Main.velkost_postavy);
                    getChildren().addAll(skin);
                    if(aura!=null) getChildren().addAll(aura);
                    //chôdza_snimok++;
                    if (chôdza_snimok==10) chôdza_snimok =1;
                }
            };
        }
    }
    public void kontrola_sipov(int cislo){  /** Kontroluje letovú zónu. */

        if (cislo <0) {cislo*=-1;}
        for(int i = 0; i<cislo; i++) {
            for (var objekt : Main.objekty) { //https://stackoverflow.com/questions/39717053/algorithm-to-verify-if-a-line-is-intersect-another-javafx
                if (objekt.typ == Objekty.Block.ARROW) {
                    if(objekt.smer_vpravo==false)objekt.setScaleX(-1);
                    for (var objekt2 : Main.objekty) {
                        if (objekt.getBoundsInParent().intersects(objekt2.getBoundsInParent())&&objekt2.typ!= Objekty.Block.ARROW) {

                            if(objekt.special){
                                if (objekt.getTranslateX() == Main.velkost/2+ objekt2.getTranslateX()) {
                                    Main.objekty.remove(objekt);
                                    if(Math.abs(objekt.getTranslateX() -  Main.hrdina.getTranslateX())<800) {
                                        Main.hrdina.setTranslateX(objekt.getTranslateX() - 10);
                                        Main.hrdina.setTranslateY(objekt.getTranslateY() - 10);
                                    }
                                    return;}//Main.MainPane.getChildren().remove(objekt);
                                else if (objekt.getTranslateX() +Main.velkost/2 == objekt2.getTranslateX()) {
                                    Main.objekty.remove(objekt);
                                    if(Math.abs(objekt.getTranslateX() -  Main.hrdina.getTranslateX())<800) {
                                        Main.hrdina.setTranslateX(objekt.getTranslateX() - 10);
                                        Main.hrdina.setTranslateY(objekt.getTranslateY() - 10);
                                    }
                                    return;}
                            }

                                if (objekt.getTranslateX() == Main.velkost/2+ objekt2.getTranslateX()) {Main.objekty.remove(objekt);return;}//Main.MainPane.getChildren().remove(objekt);
                             else if (objekt.getTranslateX() +Main.velkost/2 == objekt2.getTranslateX()) {Main.objekty.remove(objekt);return;} //Main.MainPane.getChildren().remove(objekt);

                        } else {
                            //if(objekt.getTranslateX()>this.getTranslateX()+150) {Main.MainPane.getChildren().remove(objekt);Main.objekty.remove(objekt);} //Pôvodca chyby
                            if(objekt.getBoundsInParent().intersects(Main.hrdina.getBoundsInParent())&& objekt.objekt_patri_nepriatelovi){
                                //System.out.println(hp);
                                if(postava==2 && aura!=null);
                                else hp = hp-15;
                                Main.MainPane.getChildren().remove(objekt);Main.objekty.remove(objekt);return;
                            }
                            for(var nepriatel: Svet.nepriatelia[Main.aktualna_uroven]){
                                if(nepriatel.hp<1) continue;
                                if(objekt.getBoundsInParent().intersects(nepriatel.getBoundsInParent())&& objekt.objekt_patri_nepriatelovi==false && objekt.special==false){
                                    nepriatel.hp = nepriatel.hp-35;
                                    Main.MainPane.getChildren().remove(objekt);Main.objekty.remove(objekt);return;
                                }

                            }

                        }
                    }
                    if(Main.objekty.contains(objekt)) {
                        if (objekt.smer_vpravo==false) objekt.setTranslateX(-1 + objekt.getTranslateX());
                        else objekt.setTranslateX(+1 + objekt.getTranslateX());
                    }
                }
            }
        }
    }
    private  boolean kontrola_rangu_pre_heal(int blok){
        boolean vysledok = false;
        for(int i=0;i<50;i++){
            if(getTranslateX()==blok-i) vysledok = true;
        }
        return vysledok;
    }
    public void kontrola_heal(){  /** Kontroluje, či hráč šlapol na heal block. */
        var bloky_na_zmenu = new ArrayList<Objekty>();
        for (var blok: Main.objekty){
            if(blok.healed==false&&1+Main.velkost_postavy+getTranslateY()== blok.getTranslateY() && kontrola_rangu_pre_heal((int) blok.getTranslateX())){
                if(blok.typ == Objekty.Block.HEAL){
                    hp = 100;
                    mag_hp = 100;
                    strelec_hp = 100;
                    war_hp = 100;
                        bloky_na_zmenu.add(blok);
                    try {
                         var clip = AudioSystem.getClip();
                        clip.open(AudioSystem.getAudioInputStream(new File("src/main/resources/heal.wav")));
                        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        gain.setValue(+5.0f); // Redukujem hlasitosť o X decibelov
                        clip.start();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace(System.out);
                    }

                }
            }
        }

        for (var k : bloky_na_zmenu){
            Main.objekty.remove(k);
            k.healed = true;
            Main.objekty.add(k);
        }
    }

    public void kontrola_vysky(int cislo){  /** Kontroluje gravitáciu. */
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
                            spád = 14;
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

}