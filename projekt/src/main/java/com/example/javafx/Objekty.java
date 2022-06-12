package com.example.javafx;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Random;

/**
 * @author Roman Božik.
 * Tu sú uložené všetky vlastnosti objektov
 */
public class Objekty extends Pane {
    public enum Block {  /** Názvy platforiem. */
        BRICK,WOODEN_TRAP_DOOR,CRYING_OBSIDIAN,STROM, INVISIBLE, ARROW, CRATE, GRASS, COBBLE, HEAL
    }
    ImageView block;  /** Tu je uložený skin. */
    boolean smer_vpravo = true;  /** Toto predstavuje smerovanie šípu. */
    boolean objekt_patri_nepriatelovi = false;  /** Tu sa určujú majetkové pomery. */
    boolean special = false;  /** Či je pôvod pšeciálny. */
    boolean ocarovany = false;  /** Či je objekt pod vplyvom mágie. */
    boolean healed = false;  /**Či objekt niekoho uzdravil. */
    Block typ;  /** Typ objektu. */


    public double nahodny_zapozny_double(){ //vygeneruje od -double po + double
         return new Random().nextDouble()* (new Random().nextBoolean() ? -1 : 1);
    }  /** Vypluje náhodný double od -1 po 1. */

    public Objekty(Block blocko, int x, int y) {  /** Konštruktor. */
        //https://docs.oracle.com/javafx/2/api/javafx/scene/effect/ColorAdjust.html
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(nahodny_zapozny_double());
        colorAdjust.setHue(nahodny_zapozny_double());
        colorAdjust.setBrightness(nahodny_zapozny_double());
        colorAdjust.setSaturation(nahodny_zapozny_double());
        Image image =new Image("tehla.png");
        if(blocko== Block.BRICK) image =new Image("tehla.png");
        if(blocko== Block.GRASS) image =new Image("trava.png");
        if(blocko== Block.ARROW) image =new Image("arrow.png");
        if(blocko== Block.CRATE) image =new Image("crate.png");
        if(blocko== Block.COBBLE) image =new Image("COBBLE.png");
        if(blocko== Block.WOODEN_TRAP_DOOR) image =new Image("dreveny_mostik.png");
        if(blocko== Block.CRYING_OBSIDIAN) image =new Image("crying_obsidian.png");
        if(blocko== Block.STROM) image =new Image("strom.png");
        if(blocko== Block.HEAL) image =new Image("healer.png");
        block = new ImageView(image);
        typ = blocko;
        //block.setPreserveRatio(true);
        block.setEffect(colorAdjust);
        int velkost = Main.velkost;
        block.setFitWidth(velkost);
        block.setFitHeight(velkost);
        setTranslateX(x);
        setTranslateY(y);
        if(typ == Block.ARROW){
            block.setFitWidth(velkost/2);
            block.setFitHeight(velkost/2);
        }
        if(typ == Block.INVISIBLE){
            block.setOpacity(0);
        }
        if(typ == Block.STROM){
            block.setFitHeight(velkost * 2);
        }

       getChildren().add(block);
        Main.MainPane.getChildren().add(this);
        Main.objekty.add(this);
    }
}