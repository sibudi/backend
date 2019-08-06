package com.yqg.common.utils;

import java.io.*;


/**
* @author Jacob
*
*/
public class TextToFieldUtils {

    public static void main(String args[]) throws FileNotFoundException {
        System.out.println(generate("temp.txt", true));
    }
    
    public static String generate(String fileName, boolean isResponse) throws FileNotFoundException {
        int count =0;
        StringBuilder sBuilder = new StringBuilder();
        StringBuilder tempLow = new StringBuilder();
        StringBuilder tempUp = new StringBuilder();
        File file = new File(TextToFieldUtils.class.getClassLoader().getResource(fileName).getPath());
        if (file.isFile() && file.exists()) {
            try {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                String line;
                try {
                    //????????
                    while ((line = reader.readLine()) != null) {
                        String[] cells = line.split("\t");
                        if (cells.length < 2) continue;
                        if (cells[1].trim().toLowerCase().equals("string")) {
                            tempLow.append("    private String ").append(cells[0].trim()).append(";\n\n");
                        } else if (cells[1].trim().toLowerCase().equals("int")) {
                            tempLow.append("    private Integer ").append(cells[0].trim()).append(";\n\n");
                        } else if (cells[1].trim().toLowerCase().equals("float")) {
                            tempLow.append("    private Float ").append(cells[0].trim()).append(";\n\n");
                        } else if (cells[0].trim().startsWith("List<")) {
                            tempLow.append("    private ").append(cells[0].trim()).append(" ").append(cells[1].trim()).append(";\n\n");
                        }
                        String desc = "";
                        for (int i = 2; i< cells.length; i++) {
                            desc += " " + cells[i];
                        }
                        desc = desc.trim();
                        if (isResponse) {
                            tempUp.append("    @ApiModelProperty(value = \"").append(desc).append("\", required = true)\n").append("    @JsonProperty\n");
                        } else if (desc.contains("??") && !isResponse) {
                            tempUp.append("    @ApiModelProperty(value = \"").append(desc).append("\", required = true)\n").append("    @JsonProperty\n    @NotNull\n");
                        } else {
                            tempUp.append("    @ApiModelProperty(value = \"").append(desc).append("\", required = false)\n").append("    @JsonProperty\n");
                        }
                        count ++;
                        sBuilder.append(tempUp.toString()).append(tempLow.toString());
                        tempLow = new StringBuilder();
                        tempUp = new StringBuilder();
                    }
                    reader.close();
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sBuilder.toString() +"\n" + count;
    }

    /**
     * ???????????????????????
     */
    public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        String content="";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // ???????????null?????
            while ((tempString = reader.readLine()) != null) {
                content+=tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return content;
    }

}
