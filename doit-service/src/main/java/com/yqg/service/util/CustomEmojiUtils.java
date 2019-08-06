package com.yqg.service.util;

import com.vdurmont.emoji.EmojiParser;
import com.yqg.common.utils.StringUtils;

public class CustomEmojiUtils {
     public static String removeEmoji(String str){
         if(StringUtils.isEmpty(str)){
             return str;
         }
         str = EmojiParser.removeAllEmojis(str);
         if(StringUtils.isEmpty(str)){
             return str;
         }

         String emojiPattern = "[\uD83C\uDC04-\uD83C\uDE1A]|[\uD83D\uDC66-\uD83D\uDC69]|[\uD83D\uDC66\uD83C\uDFFB-\uD83D\uDC69\uD83C\uDFFF]|[\uD83D" +
                 "\uDE45\uD83C\uDFFB-\uD83D\uDE4F\uD83C\uDFFF]|[\uD83C\uDC00-\uD83D\uDFFF]|[\uD83E\uDD10-\uD83E\uDDC0]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEF6]";

         return str.replaceAll(emojiPattern,"");
     }
}
