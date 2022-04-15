import java.io.*;
import java.lang.annotation.IncompleteAnnotationException;
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
    /**
     * UsedMemory
     * 
     */
    public class UsedMemory{
        private int start;
        private int end; 
        private int id;
        public UsedMemory(int start, int end, int id){
           this.start = start;
           this.end = end;
           this.id = id;
        }
        public int getStart(){
            return start;
        }
        @Override
        public String toString() {
            return "start: " + start + "end: " + end + "ProcessId: " + id;
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
    public void sortMemList(ArrayList<MemSlot> cpyMemList){
        Collections.sort(cpyMemList, new Comparator<MemSlot>() {
           @Override 
           public int compare(MemSlot mem1, MemSlot mem2){
               return Integer.compare(mem1.size, mem2.size);

           } 
        });
    } 
    public void reverseSortMemList(ArrayList<MemSlot> cpyMemList){
        Collections.sort(cpyMemList, new Comparator<MemSlot>() {
           @Override 
           public int compare(MemSlot mem1, MemSlot mem2){
               return Integer.compare(mem1.size, mem2.size) *-1;

           } 
        });
    } 
    public void sortProcessList(ArrayList<ProcessData> cpyProcessList){
        Collections.sort(cpyProcessList, new Comparator<ProcessData>() {
           @Override
           public int compare(ProcessData data1, ProcessData data2){
               return Integer.compare(data1.size, data2.size);
           } 
        });
    }
    public void sortUsedMem(ArrayList<UsedMemory> usedMemList){
        Collections.sort(usedMemList, new Comparator<UsedMemory>(){
            @Override
            public int compare(UsedMemory mem1, UsedMemory mem2){
                return Integer.compare(mem1.getStart(), mem2.getStart()); 
            }
        });
    }
    public void firstFit() throws IOException{
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
        for (ProcessData process : cpyProcessList) {
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
        ArrayList<UsedMemory> usedMemList = new ArrayList<>();
        ArrayList<Integer> notInserted = new ArrayList<>();

        for (MemSlot item : memSlotList){ 
            cpyMemList.add(new MemSlot(item));
        }
        for (ProcessData item : processDataList) {
           cpyProcessList.add(new ProcessData(item)); 
        }
        sortMemList(cpyMemList);
        for (ProcessData process : cpyProcessList) {
           for (MemSlot memSlot : cpyMemList) {
               //Check if the process is not already inserted and that their is enough space for the process
               if(memSlot.size-process.size >= 0 && process.size != -1){
                   memSlot.size -= process.size;
                   usedMemList.add(new UsedMemory(memSlot.start, memSlot.start + process.size, process.id));
                   memSlot.start += process.size;
                   process.size = -1; 
               }
           }
           sortMemList(cpyMemList);
           if(process.size != -1){
               notInserted.add(process.id);
           }
        }

        sortUsedMem(usedMemList);
        for (UsedMemory item : usedMemList) {
            res.add(item.start);
            res.add(item.end);
            res.add(item.id);
        }
        outputFile("BF", res, notInserted);
    }
    public void worstFit() throws IOException{
        ArrayList<MemSlot> cpyMemList = new ArrayList<>();
        ArrayList<ProcessData> cpyProcessList = new ArrayList<>();
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<UsedMemory> usedMemList = new ArrayList<>();
        ArrayList<Integer> notInserted = new ArrayList<>();

        for (MemSlot item : memSlotList){ 
            cpyMemList.add(new MemSlot(item));
        }
        for (ProcessData item : processDataList) {
           cpyProcessList.add(new ProcessData(item)); 
        }
        reverseSortMemList(cpyMemList);
        for (ProcessData process : cpyProcessList) {
           for (MemSlot memSlot : cpyMemList) {
               //Check if the process is not already inserted and that their is enough space for the process
               if(memSlot.size-process.size >= 0 && process.size != -1){
                   memSlot.size -= process.size;
                   usedMemList.add(new UsedMemory(memSlot.start, memSlot.start + process.size, process.id));
                   memSlot.start += process.size;
                   process.size = -1; 
               }
           }
           reverseSortMemList(cpyMemList);
           if(process.size != -1){
               notInserted.add(process.id);
           }
        }
        sortUsedMem(usedMemList);
        for (UsedMemory item : usedMemList) {
            res.add(item.start);
            res.add(item.end);
            res.add(item.id);
        }
        outputFile("WF", res, notInserted);
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
        mem.bestFit();
        mem.worstFit();
    }
}