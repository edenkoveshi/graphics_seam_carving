package com.company;
import java.awt.Color;
//import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.lang.Math;

public class Main {

    /*public static final int ERROR = -1;
    public static final int SUCCESS = 0;*/

    public static void main(String[] args) {
	    String path = args[0];
        BufferedImage picture = null;
        try {
            picture = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int nrows = Integer.parseInt(args[1]);
        if(nrows < 0){
            System.out.println("Illegal parameters.Please try again");
            return;
        }
        int ncols = Integer.parseInt(args[2]);
        if(ncols < 0){
            System.out.println("Illegal parameters.Please try again");
            return;
        }
        int energy_type = Integer.parseInt(args[3]);
        if(energy_type != 0 && energy_type != 1 && energy_type != 2){
            System.out.println("Illegal parameters.Please try again");
            return;
        }

        int width = picture.getWidth();
        int height = picture.getHeight();

        int[][] rgb = new int[height][width];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                rgb[i][j] = picture.getRGB(i,j);
            }
        }
    }

    public int[] getPixelRGB(int pixel){
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        int[3] rgb = {red,green,blue}
        return rgb;
    }

    public int getPixelGrayscale(int pixel){
        int[] rgb = getPixelRGB(pixel);
        return (rgb[0] + rgb[1] + rgb[2])/3;
    }

    public double[][] ComputeEnergy(int[][] pxls) {
        double[][] energy_table = int[pxls.length][pxls[0].length];
        for (int i = 0; i < pxls.length; i++) {
            for (int j = 0; j < pxls[0].length; j++) {
                int num_of_neighbors = 0;
                double pxl_energy = 0;
                if (i != 0) {
                    num_of_neighbors++;
                    pxl_energy += ComputeDistance(getPixelRGB(pxls[i][j])
                            , getPixelRGB(pxls[i - 1][j]));
                    if (j != 0) {
                        num_of_neighbors++;
                        pxl_energy += ComputeDistance(getPixelRGB(pxls[i][j])
                                , getPixelRGB(pxls[i - 1][j - 1]));
                    }
                    if (j != pxls[0].length - 1) {
                        num_of_neighbors++;
                        pxl_energy += ComputeDistance(getPixelRGB(pxls[i][j])
                                , getPixelRGB(pxls[i - 1][j + 1]));
                    }
                }
                if (i != pxls.length - 1) {
                    num_of_neighbors++;
                    pxl_energy += ComputeDistance(getPixelRGB(pxls[i][j])
                            , getPixelRGB(pxls[i + 1][j]));
                    if (j != 0) {
                        num_of_neighbors++;
                        pxl_energy += ComputeDistance(getPixelRGB(pxls[i][j])
                                , getPixelRGB(pxls[i + 1][j - 1]));
                    }
                    if (j != pxls[0].length - 1) {
                        num_of_neighbors++;
                        pxl_energy += ComputeDistance(getPixelRGB(pxls[i][j])
                                , getPixelRGB(pxls[i + 1][j + 1]));
                    }
                }
                if (j != 0) {
                    num_of_neighbors++;
                    pxl_energy += ComputeDistance(getPixelRGB(pxls[i][j])
                            , getPixelRGB(pxls[i][j - 1]));
                }
                if (j != pxls[0].length - 1) {
                    num_of_neighbors++;
                    pxl_energy += ComputeDistance(getPixelRGB(pxls[i][j])
                            , getPixelRGB(pxls[i][j + 1]));
                }
                energy_table[i][j] = pxl_energy / num_of_neighbors;
            }
        }
        return energy_table;
    }

    public double ComputeDistance(int[] pxl1,int[] pxl2){
        int r1,g1,b1 = pxl1[0],pxl1[1],pxl1[2];
        int r2,g2,b2 = pxl2[0],pxl2[1],pxl2[2];
        int d_r = Math.abs(r1-r2);
        int d_g = Math.abs(g1-g2);
        int d_b = Math.abs(b1-b2);
        return (double)(d_r + d_g + d_b)/3.0;
    }

    public double[][] ComputeEntropy(int[][] grayvals){
        double[][] entropy = new int[grayvals.length][grayvals[0].length];
        double[][] probs = ComputeProbs(grayvals);
        for(int i=0;i<grayvals.length;i++){
            int min_i = Math.max(0,i-4);
            int max_i = Math.min(grayvals.length,i+5);
            for(int j=0;j<grayvals[0].length;j++){
                int min_j = Math.max(0,j-4);
                int max_j = Math.min(grayvals.length,j+5);
                double sum = 0;
                for(int k=min_i;k<max_i;k++){
                    for(int l=min_j;l<max_j;l++){
                        sum -= probs[i,j] * Math.log(probs[i,j]);
                    }
                }
                entropy[i,j] = sum;
            }
        }
    }

    public double[][] ComputeProbs(int[][] grayvals){
        double[][] probs = new int[grayvals.length,grayvals[0].length];
        for(int i=0;i<grayvals.length;i++){
            int min_i = Math.max(0,i-4);
            int max_i = Math.min(grayvals.length,i+5);
            for(int j=0;j<grayvals[0].length;j++){
                int min_j = Math.max(0,j-4);
                int max_j = Math.min(grayvals.length,j+5);
                double sum = 0;
                for(int k=min_i;k<max_i;k++){
                    for(int l=min_j;l<min_j;l++){
                        sum += grayvals[k,l];
                    }
                }
                probs[i,j] = (double)(grayvals[i,j]) / sum;
            }
        }
    }

    public double[][] dpMap(double[][] energy){
        double[][] map = new double[energy.length][energy[0].length];
        for(int j=0;j<energy[0].length;j++){
            map[0,j] = energy[0,j];
        }
        for(int i=1;i<energy[0];i++){
            for(int j=0;j<energy[0].length;j++){
                min_j = Math.max(j-1,0);
                max_j = Math.min(j+1,energy[0].length-1);
                map[i,j] = energy[i,j] + Math.min(map[i-1,min_j],map[i-1,j]
                ,map[i-1,max_j]);
            }
        }
    }

    public double[][] straightDpMap(double[][] energy){
        double[][] map = new double[energy.length][energy[0].length];
        for(int j=0;j<energy[0].length;j++){
            map[0,j] = energy[0,j];
        }
        for(int i=1;i<energy[0];i++){
            for(int j=0;j<energy[0].length;j++){
                map[i,j] = energy[i,j] + map[i-1,j];
            }
        }
        return map;
    }

}
