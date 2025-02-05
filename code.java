import java.util.ArrayList;
import java.io.*;

class Song {

    private String title, genre;
    Singer singer;
    private Studio studio;
      
    public Song(String title, String genre,Singer singer)
    {
    
        this.studio = null;
        this.genre = genre;
        this.singer = singer;
        this.title = title;
     
    }
    
        
    public int getEstEarnings() {
        //Q2. Perform this calculation
        return this.title.length() * this.singer.getMinistry().getSongPartEst();
    }
    
    public int getClaimableEarnings() {
        int est = getEstEarnings();
        if (est > singer.getMinistry().getMinClaimable())
             return est;
        else
             return 0;
    }
    
    public String getTitle(){
        return title;
    }
    
    public void setStudio(Studio studio) {
        this.studio = studio;
    }
    
  
    
    /* 	Q4A. you will implement setStudio somewhere around here */
 
        
    public Studio getStudio(){
       return studio;
    }
    private boolean isBestSeller() {
         return getEstEarnings()>singer.getMinistry().getBestSellLimit();
    }

    
    public boolean hasStudio() {
         return (!(studio==null));
    }
     
   public String toString() {
         String outval ="Released ";
        if (isBestSeller())
            outval+="**";
        int cost =0;
        if (!(studio==null))
           cost = studio.getCost();   
        outval+=title+" as a "+genre + " single to earn $"+String.format("%,d", getEstEarnings())+".";

        return outval;
    }
}


class Singer {

    private String name="", genre;
    private int budget;
    private Studio favStudio;
    private ArrayList<Song> songs, registeredSongs;
    private Ministry ministry;
    private int grantValue;
    private String grantMessage;
    private boolean willApply;

    public     Singer(String n, String g, int b, Ministry min, boolean wantToApply ) {
        //question 1a
        this.ministry= min;
        this.name = n;
        this.genre = g;
        this.budget = b;
        this.songs= new ArrayList< Song>();
        this.registeredSongs= new ArrayList< Song>();
        /*Q1a. Add code to complete this constructor*/
        this.grantMessage ="";
        this.favStudio=null;
        this.willApply = wantToApply;

    }

    public     Singer(String name, String genre, int budget, int fav, Ministry ministry, boolean willApply ) {
        this(name, genre, budget, ministry, willApply); 
        this.favStudio= ministry.getStudio(fav);
       /*Q1b. Add code to complete this constructor*/

    }

    public Ministry getMinistry() {
        return ministry;
    }

    public boolean applying(){
        return willApply;
    }
    public boolean studioExists(Studio favStudio)
    {
        return (favStudio!=null) ;
    }

    public boolean canAfford(Studio studio)
    {
        return studio.getCost() <= budget;
    }
  
    public int getGrantValue(){
        return grantValue;
    }

    public int sumRegisteredSongs() {
        int sum = 0;
        for (Song regsong:registeredSongs)
            sum+=regsong.getClaimableEarnings();

        return sum;
    }
    public int sumEstValue() {
        //Using the Stream API to find sum of earnings.
        return this.songs.stream()
            .mapToInt(Song::getEstEarnings)
            .sum();
    }
    
    public String getName() {
        return name;
    }

    
        
    public void tryToRegisterSong(Song song) {
      Studio selectedStudio=null;
        String str= "Registering "+song.getTitle()+" with budget $"+ String.format("%,d",budget) + ".";
        if (favStudio==null)
            str = str + " No preferred studio.";
        else
        {
            str = str + "Prefers "+favStudio.getName();
            if (!favStudio.isAvailable())
                str+="(Not available).";
            else{
                str+="(Available:cost[$"+String.format("%,d",favStudio.getCost())+"]).";
                selectedStudio=favStudio;
            }
      
        }
         
        System.out.println(str);
        //////////IN THIS METHOD, DO NOT MODIFY ABOVE THIS LINE /////////////////////

         if (selectedStudio!=null){
           //Add code here to compelete Q4B so that if the selected studio is available,    
            // the studio on the song is set to the selectedstudio
             
             if(selectedStudio.isAvailable()) {
                 if(this.canAfford(selectedStudio)) {
                     song.setStudio(selectedStudio);
                 }
            
             }
            
    
        }
          
         if (!(song.hasStudio())){
             //Code to get the best studio from the ministry and assign it on song if not null and the singer can afford
             
             Studio bestAvailableStudio = null;
             bestAvailableStudio = this.ministry.getBestAvailStudio(this.budget, this.favStudio);
             
             if((bestAvailableStudio != null) && this.canAfford(bestAvailableStudio)) {
                 song.setStudio(bestAvailableStudio);
             }
             
             
            }
              
          
        
        
     
       if (song.hasStudio()){
            registeredSongs.add(song);
            budget -= song.getStudio().getCost();
            //code to reserve the studio's time could go here
           song.getStudio().reserve();
       }
    

      
      
   
            
    }
    

    
    public void addSong(String title){
            songs.add(new Song(title, genre, this ));
    }
    
    public ArrayList<Song> getSongs(){
        return songs;
    }

    public void applyForGrant() {
        for (Song song:songs)
            tryToRegisterSong(song);
        String response = ministry.processGrant(this);
        String[] responseParts = response.split(";");
        grantValue = Integer.parseInt(responseParts[0]);
        grantMessage =responseParts[1];
    }
    
    
    public String toString(){
        String str="";
        str+="-----------------------------------------------------------------\n";
        str+=name.toUpperCase();
        if (studioExists(favStudio))
            str+="["+favStudio.getName()+"]";
        if (grantValue>0)
        {
            str+="::GRANTED $"+String.format("%,d", grantValue)+"\n";
            str+="SONGS SUPPORTED\n";
            for(int i=0; i<registeredSongs.size();i++)
                str +=registeredSongs.get(i)+"\n";
        }
        else
            str+="::"+grantMessage+"\n";
           
        return str;
        
    }

}

class Studio {

    private String name;
    private int freetime;
    private int cost, recordTime;

    public Studio(String name, int freetime, int cost, int recordTime)
    {
        this.name = name;
        this.freetime = freetime;
        this.cost = cost;
        this.recordTime = recordTime;
        
    }
    
    public String getName(){
        return name;
    }

    public int getFreeTime()
    { 
        return freetime;
    }
    public Boolean isAvailable()
    {
        return freetime >= recordTime;
    }

    public void reserve()
    {
        freetime -=recordTime;
    }

    public int getCost() {

        return cost;
    }
}

//package lab2;

class Ministry {
    private Studio[] studios;
    private int grantPool;
    private int minGrantVal;
    private final int MINCLAIMABLE =0;//50000;
    private int songPartEst, bestSellLimit;
    private boolean suggestStudios;
    private ArrayList<Singer> singers;
    


    public Ministry(int grantPool, int minGrantVal, int songPartEst, int bestSellLimit, boolean suggestStudios) {
        studios = new Studio[5];
        studios[0]= new Studio("Ruff Gong", 12, 10000, 3);
        studios[1]= new Studio("Studio One", 12, 20000, 3);
        studios[2]= new Studio("Rich Entertainment",12, 60000, 3);
        studios[3]= new Studio("Eight76 Music", 12,80000,3);
        studios[4]= new Studio("Juss Buss",12, 160000, 3);
        
        this.minGrantVal = minGrantVal;
        this.grantPool=grantPool;
        this.bestSellLimit = bestSellLimit;
        this.songPartEst=songPartEst;
        this.suggestStudios = suggestStudios;
        singers = new ArrayList<Singer>();
    }

    public Studio getStudio(int id) {
        return studios[id];
    }
    
    public int getSongPartEst() {
        return songPartEst;
    }
    public int getBestSellLimit() {
        return bestSellLimit;
    }

    public int getMinClaimable() {
        return MINCLAIMABLE;
    }

    public String processGrant(Singer singer) {
        int grantVal=0;
        String grantMsg=" ";
        int singval = singer.sumRegisteredSongs();
        System.out.println(">>Processing request from " +singer.getName()+" for $"+String.format("%,d",singval));
        if (singval > MINCLAIMABLE){

            singers.add(singer);
            if (singval < minGrantVal)
                grantMsg ="Request for $"+String.format("%,d", singval)+" declined:minimum intellectual property for grants is $"+String.format("%,d", minGrantVal);
            else
            {
                if (singval > grantPool)
                    grantMsg ="Request for $"+String.format("%,d", singval)+" declined:Insufficient funds in grant pool";
                else {
                    grantVal = singval;
                    grantMsg = "$"+String.format("%,d", grantVal) +" granted to "+singer.getName();
                    grantPool -=grantVal;
                }
            }
        }
        return grantVal+";"+grantMsg;
    }

    public void showAwarded(boolean awarded){
        singers.forEach((singer)->{
              if((singer.getGrantValue()>0)==awarded)
                  System.out.println(singer);});
        //ANOTHER WAY TO DO THIS IS ....
        ///for(int i=0; i<singers.size();i++)
        ///    if((singers.get(i).getGrantValue()>0)==awarded)
        //          System.out.println(singers.get(i));
            
    }
    
    public int countSingers(){
        return singers.size();
    }
    public Studio getBestAvailStudio(int budget, Studio favStudio)
    {
        //studio already stored in sorted order of cost... no need to sort
        boolean found =false;
        Studio retStudio=favStudio;
        int foundDx =-1; 
        if (suggestStudios)
        {
            for (int dx=0; ((dx<studios.length)&&(!found));dx++)
            {
                if (!found)
                {

                    if(studios[dx].isAvailable())
                    {
                        System.out.println(">>"+studios[dx].getName() +" is available for $"+String.format("%,d", studios[dx].getCost())+ ".");
                        found = (budget >=studios[dx].getCost());
                        if (found)
                        {
                            foundDx=dx;
                            retStudio = studios[dx];

                        }
                    }
                    else
                       System.out.println(">>"+studios[dx].getName() +"is not available.");


                }

            }

            if (retStudio==null)
                System.out.println(">>No studio available for $"+String.format("%,d", budget)+".");
            else
                System.out.println(">>Assigned studio "+retStudio.getName()+".");
        }

        return retStudio;

    }
  
  
}

public class Driver{

  public static void main(String[] args) throws IOException {
        int numGrants=0;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
         String[] preMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
         int t = Integer.parseInt(preMultipleInput[0]);
         
        int grantPool = Integer.parseInt(preMultipleInput[1]);
        int minGrantVal= Integer.parseInt(preMultipleInput[2]);
        int songPartEst= Integer.parseInt(preMultipleInput[3]);
        int bestSellLimit= Integer.parseInt(preMultipleInput[4]);
        String suggestStudios = preMultipleInput[5];
        String showSummary = preMultipleInput[6];
        boolean willSuggest = false;
        boolean summary = false;
        if (suggestStudios.equals("Yes"))
            willSuggest= true;
        if (showSummary.equals("Yes"))
            summary= true;      
       Ministry min = new Ministry(grantPool,minGrantVal,songPartEst,bestSellLimit, willSuggest);
       for(int test =0; test<t; test++)
        {
            try {
              Singer  sg;
              String[] firstMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
                String name = firstMultipleInput[0];
                String genre = firstMultipleInput[1];
                int budget = Integer.parseInt(firstMultipleInput[2]);
                String applying = firstMultipleInput[3];
                boolean willApply = false;
                if (applying.equals("Yes"))
                    willApply = true;
                if (firstMultipleInput.length ==4){
                  sg = new Singer(name, genre, budget,min, willApply);
                  System.out.println("------\nSinger\n------\n"+sg);
               }
              else
              {
                     int fav = Integer.parseInt(firstMultipleInput[4]);
                     sg = new Singer(name, genre, budget,fav, min, willApply); 
                     System.out.println("------\nSinger\n------\n"+sg);
              }
              int numSongs = Integer.parseInt(bufferedReader.readLine().trim());
              for (int snum=0; snum<numSongs;snum++)
              {
                    
                     sg.addSong(bufferedReader.readLine());
                }//for anum
                if (sg.getSongs().size()>0)
                    System.out.println("-------\nSong(s)\n-------\n");
               for(Song s:sg.getSongs())
                   System.out.println(s);
               if (summary &&(sg.sumEstValue()>0)){
                  System.out.println("-------------------------------------------");
                  System.out.println("Total earnable for "+sg.getName()+" = $"+String.format("%,d",sg.sumEstValue()));
               }
               
                if (sg.applying()){
                     sg.applyForGrant();
               }
               numGrants+=(sg.getGrantValue()>0?1:0); 
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
      
     bufferedReader.close();
      if (numGrants>0)
      {
         System.out.println("=====================GRANTS AWARDED========================");
          min.showAwarded(true);
      }
      if (numGrants<min.countSingers())
          {
           System.out.println("====================APPLICATIONS NOT GRANTED====================");
           min.showAwarded(false);
          }
    }

    
}

