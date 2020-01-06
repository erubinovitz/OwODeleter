package com.minhaskamal.alphabetRecognizer.MainPackage;

import com.minhaskamal.alphabetRecognizer.Predict;
import com.minhaskamal.alphabetRecognizer.Train;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main  extends ListenerAdapter implements Runnable {
    static HashMap<Long,Long> servers = new HashMap();
    static String myToken ="";//INSERT YOUR TOKEN HERE!!
    static String[] unacceptables;
    static String[] insults;
    static String[][] wordCreators = new String[3][];
    static char[][] charCreators = new char[3][];
    static HashMap<String, Integer> forbiddenWords;
    static HashMap<Character,Integer> forbiddenChars;
    static ArrayList<String> admins = new ArrayList<>();
    static ArrayList<String> superAdmins = new ArrayList<>();
    static ArrayList<String> log = new ArrayList<>();
    static HashMap<Long,Boolean>  silentMap;
    static HashMap<Long, Boolean> lurkMap;
    static int unacceptableSize;
    static Searcher searcher;
    /*File path to read insults from.*/
    static String srcPath="../../../../../../../../src/main/java/com/minhaskamal/alphabetRecognizer/";
 /*   static String insultFilePath="../insults/";
    static String termFilePath="../Terms";
    static String adminFilePath="../resources/admins.txt";
    static String superAdminFilePath="../resources/superAdmins.txt";
    static String insultListPath="../resources/insultList.txt";*/
    static String insultFilePath="/home/ubuntu/insults/";
    static String termFilePath="/home/ubuntu/Terms";
    static String logFilePath="/home/ubuntu/resources/log.txt";
    static String adminFilePath="/home/ubuntu/resources/admins.txt";
    static String superAdminFilePath="/home/ubuntu/resources/superAdmins.txt";
    static String insultListPath="/home/ubuntu/resources/insultList.txt";
    /*File path where we want our dummy image; this image will be replaced with the
    latest image sent in order to make sure the bot is not constantly downloading and
    saving new images.
     */
    static String downloadImageFilePath="/home/ubuntu/resources/testPic.png";
    String myDiscordToken="";
    public static void printArray(String[] arr){
        for (int i=0; i<arr.length; i++){
            System.out.print(arr[i]+" ");
        }
        System.out.println();
    }
    public static void initializeLog(){
        try {
            /*File insultFolder = new File(adminFilePath);

            Scanner sc = new Scanner(insultFolder);*/
            /*   InputStream in = Main.class.getResourceAsStream(adminFilePath);*/
            InputStream in = new FileInputStream(logFilePath);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));

            String str;

            while ((str = bufReader.readLine()) != null) {
                System.out.println("adding " + str);
                log.add(str);
            }




        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void initializeAdmins(){
        try {
            /*File insultFolder = new File(adminFilePath);

            Scanner sc = new Scanner(insultFolder);*/
         /*   InputStream in = Main.class.getResourceAsStream(adminFilePath);*/
            InputStream in = new FileInputStream(adminFilePath);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));

            String str;

            while ((str = bufReader.readLine()) != null) {
                System.out.println("adding " + str);
                admins.add(str);
            }




        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void initializeSuperAdmins(){
        try {
           /* InputStream in = Main.class.getResourceAsStream(superAdminFilePath);*/
            InputStream in = new FileInputStream(superAdminFilePath);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));

            String str;

            while ((str = bufReader.readLine()) != null) {
                System.out.println("adding " + str);
                superAdmins.add(str);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public Main(){

    }
    long lastMessageTime=0;
    public boolean contains (ArrayList<String> arr, String s){
        return admins.contains(s);
    }
    public boolean addAdmin(String s){
        if (admins.contains(s))return false;
        try {
            OutputStream output = new FileOutputStream(adminFilePath,true);
            output.write(("\n"+s).getBytes());
            admins.add(s);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static void main(String[] args) throws LoginException {

            initializeUnacceptables();
            initializeInsults();
            initializeAdmins();
            initializeSuperAdmins();
            Train train = new Train();
            silentMap = new HashMap<>();
            lurkMap = new HashMap<>();
            searcher = new Searcher(forbiddenWords,forbiddenChars);
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            String token = myToken;
            builder.setToken(token);
            builder.addEventListeners(new Main());
            builder.build();


    }
    /*Returns a random insult from the insults array.*/
    public String getRandomInsult(){
        int b =(int)(Math.random()*insults.length);
       // System.out.println(b);
        return insults[b];
    }


    /*Reads text from all text files in the insults folder into the insults array.*/
    public static void initializeInsults(){



        try {
           // InputStream in = Main.class.getResourceAsStream(insultListPath);
            InputStream in = new FileInputStream(insultListPath);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
            ArrayList<String> arr = new ArrayList<>();
            String str;
            while ((str = bufReader.readLine()) != null) {
                arr.add(str);
            }
            insults = new String[arr.size()];



            for (int i=0; i<arr.size(); i++){

                    if(arr.get(i).endsWith("jpg")){
                        insults[i]=insultFilePath+arr.get(i);
                        continue;
                    }
                  //  System.out.println("doing "+insultFilePath+arr.get(i));
                //in = Main.class.getResourceAsStream(insultFilePath+arr.get(i));
                in = new FileInputStream(insultFilePath+arr.get(i));
                bufReader = new BufferedReader(new InputStreamReader(in));
                insults[i]="";
                while ((str = bufReader.readLine()) != null) {



                      //  System.out.println("\""+s+"\"");
                        if (str.equals("\\n")) {
                            insults[i] += "\n";
                           // System.out.println("X");
                        }
                        else
                            insults[i]+=str;

                    }
                    //System.out.println((i+1)+": "+insults[i]);
                }

            }
        catch (IOException e){
            System.err.println("Could not initialize insults" );
            e.printStackTrace();
        }
    }


    public static void initializeUnacceptablesHash() {
        Scanner sc;
        String[] paths = new String[]{"/starting","/middle","/transitioning"};
        String starting="!";
        forbiddenChars = new HashMap<>();
        forbiddenWords = new HashMap<>();
        try {
            for (int a = 0; a < 3; a++) {

                System.out.println("path is " +termFilePath + paths[a] + ".txt");

                String str;

                //InputStream in = Main.class.getResourceAsStream(termFilePath+paths[a]+".txt");
                InputStream in = new FileInputStream(termFilePath+paths[a]+".txt");
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));




/*
                wordCreators[a] = new String[sizeStr];
                charCreators[a]=new char[sizeChr];
                sizeChr=0;
                sizeStr=0;*/
                //in = Main.class.getResourceAsStream(termFilePath+paths[a]+".txt");
                in = new FileInputStream(termFilePath+paths[a]+".txt");
                bufReader = new BufferedReader(new InputStreamReader(in));
                while ((str = bufReader.readLine()) != null) {
                    if (str.startsWith(starting)){
                        forbiddenChars.put( (char)(Integer.parseInt(str.substring(starting.length()))),a);
                    }
                    else
                       forbiddenWords.put(str,a);
                }
                //in = Main.class.getResourceAsStream(termFilePath+paths[a]+"Emojis.txt");
                in = new FileInputStream(termFilePath+paths[a]+"Emojis.txt");
                bufReader = new BufferedReader(new InputStreamReader(in));
                while ((str = bufReader.readLine()) != null) {
                    if (str.startsWith(starting)){
                        forbiddenChars.put( (char)(Integer.parseInt(str.substring(starting.length()))),a);
                    }
                    else
                        forbiddenWords.put(str,a);
                }



            }
        }

        catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(forbiddenChars.toString());
        System.out.println("\n\n"+forbiddenWords.toString());

    }

    /*Initializes all banned words.*/
    public static void initializeUnacceptables() {
        Scanner sc;
        String[] paths = new String[]{"/starting","/transitioning","/middle"};
        String starting="!";
        try {
            for (int a = 0; a < 3; a++) {

                System.out.println("path is " +termFilePath + paths[a] + ".txt");
                int sizeStr = 0;
                int sizeChr=0;
                String str;

                //InputStream in = Main.class.getResourceAsStream(termFilePath+paths[a]+".txt");
                InputStream in = new FileInputStream(termFilePath+paths[a]+".txt");
                System.out.println(Main.class.toString());
                System.out.println(Main.class.getPackage().getName());
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
                System.out.println("pogU");

                while ((str = bufReader.readLine()) != null) {
                    System.out.println(str);
                    if (str.startsWith(starting)){
                        sizeChr++;
                    }
                    else
                        sizeStr++;
                }

                //in = Main.class.getResourceAsStream(termFilePath+paths[a]+"Emojis.txt");
                 in = new FileInputStream(termFilePath+paths[a]+"Emojis.txt");
                bufReader = new BufferedReader(new InputStreamReader(in));
                while ((str = bufReader.readLine()) != null) {
                    if (str.startsWith(starting)){
                        sizeChr++;
                    }
                    else
                        sizeStr++;
                }




                wordCreators[a] = new String[sizeStr];
                charCreators[a]=new char[sizeChr];
                sizeChr=0;
                sizeStr=0;
                //in = Main.class.getResourceAsStream(termFilePath+paths[a]+".txt");
                in = new FileInputStream(termFilePath+paths[a]+".txt");
                bufReader = new BufferedReader(new InputStreamReader(in));
                while ((str = bufReader.readLine()) != null) {
                    if (str.startsWith(starting)){
                        charCreators[a][sizeChr++] = (char)(Integer.parseInt(str.substring(starting.length())));
                    }
                    else
                        wordCreators[a][sizeStr++] = str;
                }
                //in = Main.class.getResourceAsStream(termFilePath+paths[a]+"Emojis.txt");
                in = new FileInputStream(termFilePath+paths[a]+"Emojis.txt");
                bufReader = new BufferedReader(new InputStreamReader(in));
                while ((str = bufReader.readLine()) != null) {
                    if (str.startsWith(starting)){
                        charCreators[a][sizeChr++] = (char)(Integer.parseInt(str.substring(starting.length())));
                    }
                    else
                        wordCreators[a][sizeStr++] = str;
                }

                printArray(wordCreators[a]);

            }
        }
        catch (Exception e){
                e.printStackTrace();
            }



        }

        public static boolean record(String s){
            try {
                OutputStream output = new FileOutputStream(logFilePath, true);
                output.write(("\n" + s).getBytes());
                log.add(s);
                return true;
            }
            catch (Exception e){
                return false;
            }
        }

    public String[] addArray(String[] arr, String msg){
        String[] ret = new String[arr.length+1];
        for (int i=0; i<arr.length; i++){
            ret[i]=arr[i];
        }
        ret[arr.length]=msg;
        return ret;
    }

    public static boolean recursiveSearch(String message, List<Member> mentioned,
                                          int startingIndex, int wordIndex,int wordLength,String capMessage){
        if (wordIndex==5) {
            //System.out.println("got here");
            int backIndex = startingIndex;
            int frontIndex = startingIndex;
            int wordLengthTemp = wordLength;
            boolean oneWord=true;
            int capitals=0;
            while (wordLengthTemp>=0&&backIndex>0){
                backIndex-=1;
                if (backIndex<0)break;
                if (message.charAt(backIndex)==' ')
                    oneWord=false;
                if (capMessage.charAt(backIndex)>='A'&&capMessage.charAt(backIndex)<='Z')
                    capitals++;
                if (message.charAt(backIndex)!=' '||wordLengthTemp==0){
                    wordLengthTemp-=1;
                }
            }
            try {
                if (message.substring(backIndex, backIndex + 3).equals("uuu") )
                    return false;
                if (capMessage.substring(backIndex, backIndex + 8).equals("coworker") ||
                        capMessage.substring(backIndex, backIndex + 8).equals("COWORKER")) {
                    return false;
                }
            }
            catch (Exception e){

            }
            if (capMessage.charAt(backIndex)>='a'&&capMessage.charAt((backIndex))<='z'&&!oneWord){
                try {
                    if (capitals * 1.0 / wordLength < .5 && capMessage.charAt(startingIndex) >= 'a' && capMessage.charAt((startingIndex)) <= 'z') {
                        return false;
                    }
                }
                catch (Exception e){

                }
            }
            if (capitals*1.0/wordLength>=.5&&!oneWord){
                try {
                    if (capMessage.charAt(startingIndex) >= 'a' && capMessage.charAt((startingIndex)) <= 'z') {

                    } else {
                        return false;
                    }
                }
                catch (Exception e){

                }
            }
            System.out.println("message was " +capMessage);
            return true;
        }
        int fakeWordIndex=wordIndex<3?wordIndex:wordIndex==3?1:0;
        for (int i=startingIndex; i<message.length(); i++) {// for each character...
            if (message.charAt(i)==' ')continue;
            if ((int)message.charAt(i)>55000&&(int)message.charAt(i)<56000)continue;
            for (int j = 0; j < wordCreators[fakeWordIndex].length; j++) { // for each forbidden character...
                if (i + wordCreators[fakeWordIndex][j].length() <= message.length() &&
                        message.substring(i, i + wordCreators[fakeWordIndex][j].length()).equals
                                (wordCreators[fakeWordIndex][j])) {
                    // if the character is that forbidden character...
                    if (recursiveSearch(message, mentioned, i + wordCreators[fakeWordIndex][j].length(),
                            wordIndex + 1,wordLength+wordCreators[fakeWordIndex][j].length()
                            ,capMessage)) {
                        return true;
                    }
                }
            }
            for (int j = 0; j < charCreators[fakeWordIndex].length; j++) { // for each forbidden character...
                if (i + 1 <= message.length() &&
                        message.charAt(i)==
                                (charCreators[fakeWordIndex][j])) {

                    // if the character is that forbidden character...
                    if (recursiveSearch(message, mentioned, i + 1,
                            wordIndex + 1,1
                            ,capMessage)) {
                        return true;
                    }
                }
            }

            for (int j = 0; j < wordCreators[2].length; j++) {
                if (wordIndex == 1 && i + wordCreators[2][j].length() <= message.length() &&
                        message.substring(i, i + wordCreators[2][j].length())
                                .equals(wordCreators[2][j])) {// if the character is that forbidden character...
                    if (recursiveSearch(message, mentioned, i + wordCreators[2][j].length(), 3,
                            wordLength+wordCreators[2][j].length(),capMessage)) {
                        return true;
                    }

                }
            }

            for (int j = 0; j < charCreators[2].length; j++) {
                if (wordIndex == 1 && i + 1 <= message.length() &&
                        message.charAt(i)==
                                (charCreators[2][j])) {// if the character is that forbidden character...
                    if (recursiveSearch(message, mentioned, i + 1, 3,
                            wordLength+1,capMessage)) {
                        return true;
                    }

                }
            }
            //System.out.println(i);

            for (int j = 0; j < wordCreators[0].length; j++) {
                if (wordIndex == 3 && i+wordCreators[0][j].length() <= message.length() &&
                        message.substring(i, i + wordCreators[0][j].length())
                                .equals(wordCreators[0][j])) {// if the character is that forbidden character...
                    if (recursiveSearch(message, mentioned, i + wordCreators[0][j].length(), 5,
                            wordLength+wordCreators[0][j].length(),capMessage)) {
                        return true;
                    }
                }
            }

            for (int j = 0; j < charCreators[0].length; j++) {
                if (wordIndex == 3 && i+1 <= message.length() &&
                        message.charAt(i)==
                                (charCreators[0][j])) {// if the character is that forbidden character...
                    if (recursiveSearch(message, mentioned, i + 1, 5,
                            wordLength+1,capMessage)) {
                        return true;
                    }
                }
            }
            if (wordIndex != 0) return false;
        }
            return false;

    }


    public static boolean scanMessage(String message, List<Member> mentioned){
        System.out.println("message length is " +message.length());
        for (int i=0; i<message.length(); i++){
            System.out.println((int)message.charAt(i));
        }
        if (message.contains("<:wow:616378571018993769>")) return true;
        if (message.toLowerCase().replaceAll("\\.","").toLowerCase().equals("owo")){
            return true;
        }
        //System.out.println(message.toLowerCase().replaceAll(".","").toLowerCase());
        for (int i=0; i<mentioned.size(); i++)
        if (recursiveSearch(mentioned.get(i).toString()
                        .toLowerCase()
                        .trim()
                //.replaceAll(" ","")
                ,mentioned,0,0,0,message)) return true;
        return recursiveSearch(message.toLowerCase()
                .trim()
                //.replaceAll(" ","")
                ,mentioned,0,0,0,message);
/*     int i=0;
     while (i<message.length()){
         Integer valStr = forbiddenWords.get(Character.toString(message.charAt(i)));
         Integer valChar = forbiddenChars.get(message.charAt(i));
         if (valChar == 1 || valStr == 1 || message.charAt(i)==' '){
             message = message.substring(0,i)+message.substring(i+1);
         }
     }
     return searcher.search(message.toCharArray());*/

    }
        public void adminCommand(String[] args, String userID, MessageReceivedEvent event){
            if (args[0].toLowerCase().equals("help")){
                String s =
                        "**Commands**:\n\n" +
                                "**urallthots**: Lets everyone else know that they are a thot. Parameters: None.\n\n" +
                                "**insult**: Displays the requested insult by insult index. Parameters: Integer (index)\n\n" +
                                "**reinitialize**: Reinitializes your choice of {admins}, {unacceptables}, or {insults} based on files." +
                                    " Parameters: String {\"unnaceptables\", \"admins\", or \"insults\"}.\n\n" +
                                "**addAdmin**: Adds the mentioned user to the admin list permanently. Parameters: String {Mention of the user}.\n\n" +
                                "**addCom**: Adds the specified character to the banlist. Parameters:" +
                                "\n*character*: the character that you'd like to ban. *index*: 0 for starting, 1 for transition," +
                                "2 for middle. *isEmote*: true if is emote, false if not.\n\n"+
                                "**silence/unsilence**: The bot will continue to moderate messages, but will keep quiet about it.\n\n" +
                                "**lurk/unlurk**: The bot will not moderate any messages.\n\n"+
                                "**log {n}**: shows the nth deleted message. If n is not present, shows number of deleted messages."
                                ;
                event.getChannel().sendMessage(s).queue();

                return;
            }
            if (args[0].toLowerCase().equals("urallthots")){

                event.getChannel().sendMessage("ur all thots.").queue();

                return;
            }
            if (args[0].toLowerCase().equals("insult")){
                if (args.length!=2){
                    event.getChannel().sendMessage("Invalid args. Try !!help for help.").queue();
                    return;
                }
                int num=0;
                try {
                     num = Integer.parseInt(args[1]);
                }
                catch (Exception e){
                    event.getChannel().sendMessage("Invalid args. Try !!help for help.").queue();
                    return;
                }
                if (num>=insults.length){
                    event.getChannel().sendMessage("Invalid insult value.").queue();
                    return;
                }
                if (insults[num].endsWith("jpg")){
                    event.getChannel().sendMessage(" ").addFile(new File(insults[num])).queue();
                }
                else {
                    event.getChannel().sendMessage(insults[num]).queue();
                }
                return;
            }
            if (args[0].toLowerCase().equals("addcom")) {

                if (args.length !=4||( !args[3].toLowerCase().equals("true")  && !args[3].toLowerCase().equals("false")
                )||args[1].replaceAll(" ","").equals("")){
                    event.getChannel().sendMessage("Invalid args. Try !!help for help.").queue();
                    return;
                }
                int num=0;
                try{
                    num=Integer.parseInt(args[2]);
                }
                catch (Exception e){
                    event.getChannel().sendMessage("Invalid args. Try !!help for help.").queue();
                    return;
                }

                String emoji = args[3].toLowerCase().equals("true")?"Emojis":"";
                String position = num==0?"starting":num==1?"transition":num==2?"middle":"error";
                try {
                    File file = new File(termFilePath+"/"+position+emoji+".txt");


                    System.out.println("adding to "+termFilePath+"/"+position+emoji+".txt");
                    char lastChar = args[1].charAt(args[1].length()-1);
                    int written = (int)lastChar;
                    OutputStream output = new FileOutputStream(termFilePath+"/"+position+emoji+".txt",true);
                    output.write(("\n!"+written).getBytes());
                    initializeUnacceptables();
                    event.getChannel().sendMessage("Successfully added.").queue();
                    return;
                }
                catch (Exception e){
                    event.getChannel().sendMessage("Invalid args. Try !!help for help.").queue();
                    e.printStackTrace();
                    return;
                }
            }
            else if (args[0].toLowerCase().equals("reinitialize")){
                if (args[1].toLowerCase().equals("insults")) {
                    initializeInsults();
                    event.getChannel().sendMessage("Insults successfully re-initialized.").queue();
                    return;
                }
                else if (args[1].toLowerCase().equals("unacceptables")) {
                    initializeUnacceptables();
                    event.getChannel().sendMessage("Unacceptables successfully re-initialized.").queue();
                    return;
                }
                else if (args[1].toLowerCase().equals("admins")) {
                    initializeAdmins();
                    event.getChannel().sendMessage("Admins successfully re-initialized.").queue();
                    return;
                }

                else{
                    event.getChannel().sendMessage("Invalid args. Try !!help for help.").queue();
                    return;
                }
            }
            else if (args[0].toLowerCase().equals("addadmin")){
                System.out.println(userID+" is the id");
                if (!contains(superAdmins,userID)){
                    event.getChannel().sendMessage("Insufficient privileges.").queue();
                    return;
                }
                else if (addAdmin(userID)){
                    event.getChannel().sendMessage("Admin privileges successfully granted.").queue();
                    return;
                }
                else{
                    event.getChannel().sendMessage("Admin privileges could not be added. It is likely that " +
                            "that user is already an admin.").queue();
                    return;
                }
            }
            else if (args[0].toLowerCase().equals("silence")){
                boolean silent = silentMap.get(event.getGuild().getIdLong());
                if (silent){
                    event.getChannel().sendMessage("I am already silent.").queue();
                    return;
                }
                else{
                    silentMap.replace(event.getGuild().getIdLong(),true);
                    event.getChannel().sendMessage("Entering silent mode.").queue();
                    return;
                }
            }
            else if (args[0].toLowerCase().equals("unsilence")){
                boolean silent = silentMap.get(event.getGuild().getIdLong());
                if (silent){
                    silentMap.replace(event.getGuild().getIdLong(),false);
                    event.getChannel().sendMessage("I am no longer silent.").queue();
                    return;
                }
                else{

                    event.getChannel().sendMessage("I am already vocal.").queue();
                    return;
                }
            }
            else if (args[0].toLowerCase().equals("lurk")){
                boolean lurking = lurkMap.get(event.getGuild().getIdLong());
                if (lurking){
                    event.getChannel().sendMessage("I am already lurking.").queue();
                    return;
                }
                else{
                    lurkMap.replace(event.getGuild().getIdLong(),true);
                    event.getChannel().sendMessage("I will now lurk.").queue();
                    return;
                }
            }
            else if (args[0].toLowerCase().equals("unlurk")){
                boolean lurking = lurkMap.get(event.getGuild().getIdLong());
                if (lurking){
                    lurkMap.replace(event.getGuild().getIdLong(),false);
                    event.getChannel().sendMessage("I am no longer lurking.").queue();
                    return;
                }
                else{

                    event.getChannel().sendMessage("I am already destroying all thots.").queue();
                    return;
                }
            }
            else if (args[0].toLowerCase().equals("log")){
                if (args.length==1){
                    event.getChannel().sendMessage("There are "+ log.size() + " deleted comments in my database.").queue();
                    return;
                }
                else{
                    try{
                        String s = log.get(Integer.parseInt(args[1])-1);
                        event.getChannel().sendMessage("Log Retreived: \""+ s+"\"").queue();
                        return;
                    }
                    catch (Exception e){
                        event.getChannel().sendMessage("Invalid args. Try !!help for help.").queue();
                        return;
                    }
                }
            }

            else{
                event.getChannel().sendMessage("Invalid args. Try !!help for help.").queue();
                return;
            }
        }
    @Override
        public void onMessageReceived(MessageReceivedEvent event){
            onMessageReceived(event,"");
        }

    public void onMessageReceived(MessageReceivedEvent event,String s){
        long id = event.getGuild().getIdLong();
        if (!servers.containsKey(id)){
            servers.put(id, (long) -5001);
            lurkMap.put(id,false);
            silentMap.put(id,false);

        }

        /*If user that sent message is a bot, return to avoid infinite loops.*/
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getName().endsWith("introductions"))
            return;
        /*Initialize all of our relevant values, along with predictor for ML algorithm.*/
        long msgID = event.getMessageIdLong();

        String user = event.getAuthor().getName();
        String userID= event.getAuthor().getId();
        String message;
        if (s.equals(""))
            message = event.getMessage().getContentRaw();
        else message=s;

        //message=message.toLowerCase();
        Predict predict = new Predict();
        List<Emote> lis = event.getMessage().getEmotes();
        int size =event.getMessage().getAttachments().size();
        userID=userID.replaceAll("!","");
        for (int b=0; b<1; b++){

        if (message.startsWith("!!")&&(contains(admins,userID)||contains(superAdmins,userID))){
            message=message.substring(2,message.length());
            String[] args = message.split(" "); // S
            adminCommand(args,userID,event);
            return;
        }}
        if (lurkMap.get(id))return;

        /*Start by looking at any attachments the user sent. If there are images,
        analyze the image using the prediction algorithm; if a violation is found,
        immediately call out the user, delete the message, and return.
         */

        for (int i=0; i<size; i++){
            System.out.println(event.getMessage().getAttachments().get(i));
            if (event.getMessage().getAttachments().get(i).isImage()){
                event.getMessage().getAttachments().get(i).downloadToFile
                        (new File(downloadImageFilePath));
                try {
                    String prediction = predict.predict();
                    if (prediction.length()>10){
                        continue;
                    }
                    boolean deletion =
                             scanMessage(prediction.toLowerCase(),
                             event.getMessage().getMentionedMembers());
                    if (deletion) {
                        String insult = getRandomInsult();
                        if (insult.endsWith("jpg")){
                            Message atUser = new MessageBuilder()
                                    .append("<@" + userID + "> ").build();
                            event.getChannel().sendMessage("<@" + userID + "> ")
                                    .addFile(new File(insult)).queue();
                        }
                        else
                            event.getChannel().sendMessage("<@" + userID + "> "+
                                insult).queue();
                        event.getChannel().deleteMessageById(msgID).queue();
                    }
                    return;
                    }
                catch (Exception e){
                    System.err.println("Could not recognize text from image.");
                }
            }
        }
        System.out.println(user+": "+message);


        /*if no violations are found, check the user's message, along with any mentioned
        users, and check them for violations.
         */
        boolean deletion =
                scanMessage(message,
                        event.getMessage().getMentionedMembers());
        boolean silent = silentMap.get(event.getGuild().getIdLong());
        if (deletion) {
            String insult = getRandomInsult();
            if (insult.endsWith("jpg")){
                if (System.currentTimeMillis()-lastMessageTime>5000&&!silent) {
                    Message atUser = new MessageBuilder()
                            .append("<@" + userID + "> ").build();
                    event.getChannel().sendMessage("<@" + userID + "> ")
                            .addFile(new File(insult)).queue();
                    lastMessageTime=System.currentTimeMillis();
                }
                event.getChannel().deleteMessageById(msgID).queue();
                return;
            }
            else {
                if (System.currentTimeMillis()-servers.get(event.getGuild().getIdLong())>5000&&!silent) {
                    event.getChannel().sendMessage("<@" + userID + "> " +
                            insult).queue();
                    servers.replace(event.getGuild().getIdLong(),System.currentTimeMillis());
                    record(user+": "+message);
                }
                event.getChannel().deleteMessageById(msgID).queue();
                return;
            }
        }

        Runnable r = new Main(event,message+":00");
        new Thread(r).start();


    }
    String msg ="";
    MessageReceivedEvent event;

    public Main (MessageReceivedEvent event, String msg){
        this.event=event;
        this.msg=msg;
    }

    public void run()
    {
        try
        {

           // System.out.println(event==null);
            String message=msg;
            int startSize=charCreators[0].length;
            int transitionSize=charCreators[1].length;
            int midSize=charCreators[2].length;

            long startingTime = System.currentTimeMillis()/1000;
            //System.out.println("looking for " +message);
            //System.out.println("looking for " +event.getMessage().toString());
            while (System.currentTimeMillis()/1000-startingTime<100){

                if (!message.equals(event.getChannel()
                        .retrieveMessageById(event.getMessageId()).complete().getContentRaw())
                ||startSize!=charCreators[0].length || transitionSize!=charCreators[1].length||midSize!=charCreators[2].length){
                   // System.out.println("not equal!");
                    onMessageReceived(event,event.getChannel()
                            .retrieveMessageById(event.getMessageId()).complete().getContentRaw());
                    break;
                }
               // if (event.getMessage().getReactions().get(i).getReactionEmote().)
                sleep(1000);
            }
            //System.out.println("done");
        }
        catch (Exception e)
        {
            // Throwing an exception
            e.printStackTrace();
        }
    }
}

