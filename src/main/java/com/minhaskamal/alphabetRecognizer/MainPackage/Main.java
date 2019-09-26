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
import java.util.List;
import java.util.Scanner;
public class Main  extends ListenerAdapter implements Runnable {

    static String myToken ="";//INSERT YOUR TOKEN HERE!!
    static String[] unacceptables;
    static String[] insults;
    static String[][] wordCreators = new String[3][];
    static String admins[] = {"161546387870187522","213323815386611713"};

    static int unacceptableSize;
    /*File path to read insults from.*/
    static String insultFilePath=new File("").getAbsolutePath()+"/src/main/java/com/" +
            "minhaskamal/alphabetRecognizer/insults";
    static String termFilePath=new File("").getAbsolutePath()+"/src/main/java/com/" +
            "minhaskamal/alphabetRecognizer/Terms";
    /*File path where we want our dummy image; this image will be replaced with the
    latest image sent in order to make sure the bot is not constantly downloading and
    saving new images.
     */
    static String downloadImageFilePath=new File("").getAbsolutePath()+"/src/main/java/com/" +
            "minhaskamal/alphabetRecognizer/resources/testPic.png";
    String myDiscordToken="";
    public static void printArray(String[] arr){
        for (int i=0; i<arr.length; i++){
            System.out.print(arr[i]+" ");
        }
        System.out.println();
    }
    public Main(){

    }
    long lastMessageTime=0;
    public boolean contains (String[] arr, String s){
        for (int i=0; i<arr.length; i++){
            if (arr[i].equals(s)) return true;
        }
        return false;
    }
    public static void main(String[] args) throws LoginException {

        initializeUnacceptables();
        initializeInsults();
        Train train = new Train();
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

    /*
    Discord bot that deletes messages having certain banwords, mainly OwO, UwU, etc

Also prints a message randomly generated from a list of messages with intent to shame the sender
     */
    /*Reads text from all text files in the insults folder into the insults array.*/
    public static void initializeInsults(){
        File insultFolder = new File(insultFilePath);
        File[] insultFiles = insultFolder.listFiles();
        insults = new String[insultFiles.length];

        Scanner sc;
        for (int i=0; i<insults.length; i++){
            try {
                if(insultFiles[i].getName().endsWith("jpg")){
                    insults[i]=insultFiles[i].getAbsolutePath();
                    continue;
                }
                sc = new Scanner(insultFiles[i]);
                insults[i]="";

                while (sc.hasNextLine()){

                    String s = sc.nextLine();
                  //  System.out.println("\""+s+"\"");
                    if (s.equals("\\n")) {
                        insults[i] += "\n";
                       // System.out.println("X");
                    }
                    else
                        insults[i]+=s;

                }
                //System.out.println((i+1)+": "+insults[i]);
            }
            catch (IOException e){
                System.err.println("Could not initialize " +insultFiles[i]);
                e.printStackTrace();
            }
        }

    }
    /*Initializes all banned words.*/
    public static void initializeUnacceptables() {
        Scanner sc;
        String[] paths = new String[]{"/starting","/transitioning","/middle"};

        try {
            for (int a = 0; a < 3; a++) {

                sc = new Scanner(new File(termFilePath + paths[a] + ".txt"));
                int size = 0;
                while (sc.hasNextLine()) {
                    size++;
                    sc.nextLine();
                }

                String str;
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream
                        (termFilePath + paths[a] + "Emojis.txt"), "UTF-8"));
                while ((str = bufReader.readLine()) != null) {
                    size++;
                }


                sc = new Scanner(new File(termFilePath + paths[a] + ".txt"));
                wordCreators[a] = new String[size];
                int index = 0;
                for (int i = 0; sc.hasNextLine(); i++) {
                    wordCreators[a][i] = sc.nextLine();
                    index++;
                }
                bufReader = new BufferedReader(new InputStreamReader(new FileInputStream
                        (termFilePath + paths[a] + "Emojis.txt"), "UTF-8"));
                while ((str = bufReader.readLine()) != null) {
                    wordCreators[a][index++] = str;
                }
                System.out.println("size is " + size);

                printArray(wordCreators[a]);

            }
        }
        catch (Exception e){
                e.printStackTrace();
            }



          /*  wordCreators[0] = new String[]{"o", "0", "u", "q", "\uD83C\uDDF4", "\u014D", "\u53E3", "\u00F8", "\uD83C\uDD7E",
                    "~", "Ø", "\u00F3", "\u00F2", "\u00FA", "\u00F9", "\u00D9", "ㅇ", "\u00F6", "\u00F5", "Ô", "Ó", "Ò", "\u2B55",
                    "Û", "Ú", "Ü", "û", "ü", "ο", "[]", "()", "é", "-", "\u0030", "\uD83D\uDD35", "\u26AB", "\uD83D\uDD34", "\u26AA",
                    "\u03BF", "\u039F", "\u110B", "\u11BC", "@", "\u00B0", "(", ")", "\uD83C\uDD7E", "\uD83C\uDD7E"
                    , "\u03B8", "\u03F4","\uD83C\uDF11"};
            printArray(wordCreators[0]);
            System.out.println(wordCreators[0].length);
            wordCreators[1] = new String[]{"__", "~", "~~", ".", "-", "_", "・", "\u30FB", "|",":"};
            wordCreators[2] = new String[]{"\u03C9", "v", "w", "w", "🇼", "w", "a", "vv", "w", "\u2C72",
                    "\u20A9", "\u019C", "-", ".", "u", "\uD83C\uDDFC", "\uD83C\uDDFB", "\u1E98", "\u03C9"};*/

            unacceptables = new String[wordCreators[0].length * wordCreators[0].length *
                    wordCreators[1].length * wordCreators[1].length * wordCreators[1].length];
            int count = 0;
            if (false)
            for (int i = 0; i < wordCreators[0].length; i++) {
                for (int j = 0; j < wordCreators[2].length; j++) {
                    for (int k = 0; k < wordCreators[0].length; k++) {
                        for (int l = 0; l < wordCreators[1].length; l++)
                            unacceptables[count++] = wordCreators[0][i] + wordCreators[1][l] +
                                    wordCreators[2][j] + wordCreators[1][l] + wordCreators[0][k];
                        unacceptableSize++;
                    }
                }
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
            return true;
        }
        int fakeWordIndex=wordIndex<3?wordIndex:wordIndex==3?1:0;
        for (int i=startingIndex; i<message.length(); i++) {// for each character...
            if (message.charAt(i)==' ')continue;
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
            if (wordIndex != 0) return false;
        }
            return false;

    }


    public static boolean scanMessage(String message, List<Member> mentioned){
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

      /*  for (int k=0; k<unacceptableSize;k++){
            if (message
                    .replaceAll(" ","")
                    .replaceAll("_","")
                    .replaceAll("\\*","")
                    .toLowerCase()
                    .contains(unacceptables[k])


            ){

                return true;
            }
            else {
                for (int j = 0; j < mentioned.size(); j++) {
                    if (mentioned.get(j).toString()
                            .replaceAll(" ", "")
                            .replaceAll("_", "")
                            .replaceAll("\\*", "")
                            .toLowerCase()
                            .contains(unacceptables[k])) {

                        return true;
                    }
                }
            }
                //System.out.println(message.trim().toLowerCase()+"!="+unacceptables[i]);
            }
        return false;*/
    }
        public boolean adminCommand(String term, int index, boolean isEmoji)throws  IOException{
            if (index==0){
                if (isEmoji){
                    File file = new File(termFilePath+"\\startingEmojis.txt");
                    FileWriter writer = new FileWriter(file,true);
                    writer.write("\n"+term);
                    writer.flush();
                    writer.close();
                    return true;
                }
            }
            else if (index==1){

            }
            else if (index==2){

            }
            else{
                return false;
            }
            return false;
        }
    @Override
        public void onMessageReceived(MessageReceivedEvent event){
            onMessageReceived(event,"");
        }

    public void onMessageReceived(MessageReceivedEvent event,String s){
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
        for (int b=0; b<1; b++){
        if (message.startsWith("!!")&&contains(admins,userID)){
            message=message.substring(2,message.length());
            String[] args = message.split(" "); // S
            if (args[0].toLowerCase().equals("urallthots")){
                if (!contains(admins,userID)){
                    break;
                }
                event.getChannel().sendMessage("ur all thots.").queue();

                return;
            }
            if (args[0].toLowerCase().equals("insult")){
                if (args.length!=2){
                    event.getChannel().sendMessage("Invalid args.").queue();
                    return;
                }
                int num = Integer.parseInt(args[1]);
                if (num>=insults.length)break;
                if (insults[num].endsWith("jpg")){

                    event.getChannel().sendMessage(" ")
                            .addFile(new File(insults[num])).queue();

                }
                else {
                    event.getChannel().sendMessage(
                            insults[num]).queue();

                }

                return;
            }


            if (args[0].toLowerCase().equals("addcom")) {
                if (!userID.equals("213323815386611713")){
                    break;
                }
                if (args.length !=4||( !args[3].toLowerCase().equals("true")  && !args[3].toLowerCase().equals("false")
                )){
                    event.getChannel().sendMessage("Invalid args.").queue();
                    return;
                }
                int num=0;
                try{
                    num=Integer.parseInt(args[2]);
                }
                catch (Exception e){
                    event.getChannel().sendMessage("Invalid args.").queue();
                    return;
                }
                boolean emoji = args[3].toLowerCase().equals("true")?true:false;
                try {
                    if (!adminCommand(args[1], num, emoji)) {
                        event.getChannel().sendMessage("Invalid args.").queue();
                        return;
                    }
                    event.getChannel().sendMessage("Successfully added.").queue();
                    return;
                }
                catch (Exception e){
                    e.printStackTrace();
                    return;
                }
            }
            else{
                event.getChannel().sendMessage("Invalid args.").queue();
                return;
            }
        }}

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
        if (deletion) {
            String insult = getRandomInsult();
            if (insult.endsWith("jpg")){
                if (System.currentTimeMillis()-lastMessageTime>5000) {
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
                if (System.currentTimeMillis()-lastMessageTime>5000) {
                    event.getChannel().sendMessage("<@" + userID + "> " +
                            insult).queue();
                    lastMessageTime=System.currentTimeMillis();
                }
                event.getChannel().deleteMessageById(msgID).queue();
                return;
            }
        }

        Runnable r = new Main(event,message);
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
            long startingTime = System.currentTimeMillis()/1000;
            //System.out.println("looking for " +message);
            //System.out.println("looking for " +event.getMessage().toString());
            while (System.currentTimeMillis()/1000-startingTime<100){

                if (!message.equals(event.getChannel()
                        .retrieveMessageById(event.getMessageId()).complete().getContentRaw())){
                   // System.out.println("not equal!");
                    onMessageReceived(event,event.getChannel()
                            .retrieveMessageById(event.getMessageId()).complete().getContentRaw());
                    break;
                }
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

