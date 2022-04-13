import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Mallocator
 */
public class Mallocator {
    ArrayList<MemSlot> memSlotList = new ArrayList<>();
    ArrayList<ProcessData> processDataList = new ArrayList<>();
    public class MemSlot{
        public int size;
        public int start; 
        public int end;

        public MemSlot(int start, int end){
            
            this.start = start;
            this.end = end;
            this.size = end-start; 

        }

        public MemSlot(MemSlot mem){
            this.start = mem.start;
            this.end = mem.end;
            this.size = mem.size;
        }

        @Override
        public String toString() {
            return "Start: " + this.start + " End: " + this.end + " Size: " + this.size; 
        }
    }
    public class ProcessData{
        public int id; 
        public int size;
        ProcessData(int id, int size){
            this.id = id;
            this.size = size; 
        }
        public ProcessData(ProcessData data){
            this.id = data.id;
            this.size = data.size;
        }
        @Override
        public String toString() {
            return "id: " + this.id + " size:  " + this.size;
        }
    }

    public void dataParser(File Min, File Pin) throws Exception{
        //Parse Mem Slot Data
        Scanner scanner = new Scanner(Min);
        String size = scanner.nextLine();
        System.out.println("Memory Slot Allocated: " + size);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] arrOfStrNum = line.split(" ", 2);
            int start = Integer.parseInt(arrOfStrNum[0]);
            int end = Integer.parseInt(arrOfStrNum[1]);
            memSlotList.add(new MemSlot(start,end));
            
        }
       //Parse Process Data 
       scanner = new Scanner(Pin);
       size = scanner.nextLine();
       System.out.println("Process Trying to Allocate: " + size);
       while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] arrOfStrNum = line.split(" ", 2);
            int id = Integer.parseInt(arrOfStrNum[0]);
            int processSize = Integer.parseInt(arrOfStrNum[1]);
            processDataList.add(new ProcessData(id, processSize));
       } 

    }
    public void readMemList(ArrayList<MemSlot> memList){
        int counter = 0;
        for (MemSlot memSlot : memList) {
            System.out.println(counter + ": " + memSlot);
            counter++;
        }
    }
    public void readProcessList(ArrayList<ProcessData> processList){
        for(ProcessData proccessData : processList){
            System.out.println(proccessData);
        }
    }
    public void firstFit() throws IOException{
        ArrayList<MemSlot> cpyMemList = new ArrayList<>();
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> notInserted = new ArrayList<>();

        for (MemSlot item : memSlotList){ 
            cpyMemList.add(new MemSlot(item));
        }
        for (ProcessData process : processDataList) {
            for (MemSlot memSlot : cpyMemList) {
                if(memSlot.size >= process.size){
                    memSlot.size = memSlot.size - process.size;
                    //Add all the data that we want to write to the file 
                    res.add(memSlot.start);
                    res.add(memSlot.start + process.size);
                    res.add(process.id);

                    //Mark the process as used 
                    process.size = -1;
                    break;
                }
            }
            if(process.size != -1) notInserted.add(process.id);
        }
        outputFile("FF", res, notInserted);
    }
    public void bestFit() throws IOException{
        ArrayList<MemSlot> cpyMemList = new ArrayList<>();
        ArrayList<ProcessData> cpyProcessList = new ArrayList<>();
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> notInserted = new ArrayList<>();

        for (MemSlot item : memSlotList){ 
            cpyMemList.add(new MemSlot(item));
        }
        for (ProcessData item : processDataList) {
           cpyProcessList.add(new ProcessData(item)); 
        }
        int processListSize = cpyProcessList.size();
        for (int i = 0; i < processListSize; i++) {
            for (MemSlot memSlot : cpyMemList) {
                int minMemSize = Integer.MAX_VALUE;
                int processId = -66; 
                for (ProcessData process : cpyProcessList) {
                    //Find the best fit update minMemSize and get id
                    if(minMemSize > memSlot.size-process.size){
                        minMemSize = memSlot.size-process.size;
                        processId = process.id;
                    }
            
                } 
               //Remove assigned process from out list
               if(processId != 66){
                   cpyProcessList.remove(processId-1);
               }
            }

            
        }

        //for (ProcessData process : processDataList) {
            //int[] arr = new int[2];
            //arr[0] = Integer.MAX_VALUE;
            //int i = 0;
            //for (MemSlot memSlot : cpyMemList) {
                //System.out.println(arr[0]);
                ////Place the remaing size in memory. Only if the remaing size is smaller than the memory already stored. And not negative.
                //if(arr[0] >  memSlot.size - process.size && memSlot.size - process.size >= 0){
                    //arr[0] = memSlot.size - process.size;
                    //arr[1] = i; 
                //}
                //i++;
            //}
            //if((Integer)arr[0] == Integer.MAX_VALUE){
                //notInserted.add(process.id);
            //}else{
                ////Update the size of the memslot used 
                //cpyMemList.get(arr[1]).size = cpyMemList.get(arr[1]).size - process.size;

                //MemSlot bestFitMemSlot = cpyMemList.get(arr[1]);
                //res.add(bestFitMemSlot.start);
                //res.add(bestFitMemSlot.end);
                //res.add(process.id);
            //} 
        //}

        readMemList(cpyMemList);
        outputFile("BF", res, notInserted);
    }
    public void outputFile(String name, ArrayList<Integer> inserted, ArrayList<Integer> notInserted) throws IOException{
        String FileName = name + "output.data";
        FileWriter dataFile = new FileWriter(FileName);
        
        for (int i = 0; i < inserted.size(); i+=3) {
            dataFile.write("\n" + inserted.get(i) + " " + inserted.get(i+1) + " " + inserted.get(i+2));
        }
        if(notInserted.isEmpty()){
            dataFile.write("\n-0");
        }else if(notInserted.size() == 1){
            dataFile.write("\n-" + notInserted.get(0));
        }else{
            dataFile.write("\n-");
            for (Integer integer : notInserted) {
                dataFile.write(integer + ", ");
            }
        }

        dataFile.close();

    }
    public void MAllocator(){ 
        
    }
    public static void main(String[] args) throws Exception {
        File Min = new File("Minput.data");
        File Pin = new File("Pinput.data");
        Mallocator mem = new Mallocator();
        mem.dataParser(Min, Pin);
        mem.firstFit();
        //mem.bestFit();
    }
}