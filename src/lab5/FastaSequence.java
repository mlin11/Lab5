package lab5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;



public class FastaSequence
{
	private String header;
	private StringBuffer sequence = new StringBuffer();
	
	//constructor
	public FastaSequence( String header, StringBuffer sequence ) 
	{
		
		this.header = header;
		this.sequence = sequence;
	}
	
	// static factory method
	public static List<FastaSequence> readFastaFile(String filePath) throws Exception
	{
		//generate a list to store header/sequence
		List<FastaSequence> list = new ArrayList<FastaSequence>();
		// read fasta file
		BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
		
		// check if the file is a fasta file
		//read the first line
		String firstLine = reader.readLine();
		String header;
		StringBuffer sequence = new StringBuffer();
		
		if(firstLine.startsWith(">"))
		{
			header= firstLine.substring(1);
			
		}else
		{
			reader.close();
			throw new Exception("Please make sure this is a fasta file!");

		}
		
		
		for(String nextLine =reader.readLine();nextLine !=null; nextLine=reader.readLine())
		{
			//read from the second line
			// the program will implement else statement first
			
			if(nextLine.startsWith(">"))
            {
				//whenever reaches a new header, save the previous header/sequence
                    FastaSequence pair = new FastaSequence(header, sequence);
                    list.add(pair);
                    // update the header with new header
                    header=nextLine.substring(1);
                    // empty the sequence stringBuffer
                    sequence.setLength(0);

            }else
            {
            	//save 1-many lines of sequence into the stringBuffer
                    sequence.append(nextLine.trim());

            }



		}
		// generate a new FastaSequence to store the last header/sequence
		list.add(new FastaSequence(header,sequence));
		//close the reader
		reader.close();
		//return the list
		return list;

		
		
	}
	
	/* add a static method to do this
	 * writes each unique sequence to the output file with the # of times each sequence was 
	 * Seen in the input file as the header (sorted with the sequence seen the fewest times the first)
	 */
	
	public static void writeUnique(File inFile, File outFile ) throws Exception
	{
		//read input file and store into a hashMap
		HashMap<String,Integer> seqCount = new HashMap<String,Integer>();
		// read fasta file
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		// check if the file is a fasta file
		//read the first line
		String firstLine = reader.readLine();
		
		if(! firstLine.startsWith(">"))
		{
			reader.close();
			throw new Exception("Please make sure this is a fasta file!");

		}
		// define new StringBuffer to store sequence
		StringBuffer sequence = new StringBuffer();
		
		
		for(String nextLine =reader.readLine();nextLine !=null; nextLine=reader.readLine())
		{
			//read from the second line-sequence line
			if(! nextLine.startsWith(">"))
            {
            		//save current line into the stringBuffer
				sequence.append(nextLine.trim());
            }
			else
			{
				// whenever reaches a header, save the previous sequence into the hashMap
				if(! seqCount.containsKey(sequence.toString()))
				{
					Integer count = 1;
					seqCount.put(sequence.toString(),count);
					
				}
				else
				{
					Integer count = seqCount.get(sequence.toString());
					count++;
					seqCount.put(sequence.toString(),count);
					
				}
				sequence.setLength(0);
					
			}
			//need figure out an efficient way to handle the last sequence inside the for loop
			
		}
		//for last line of last sequence
		if(! seqCount.containsKey(sequence.toString()))
		{
			Integer count = 1;
			seqCount.put(sequence.toString(),count);
			
		}
		else
		{
			Integer count = seqCount.get(sequence.toString());
			count++;
			seqCount.put(sequence.toString(),count);
			
		}
		//close the reader
		reader.close();
		
		// sort by the value of the hashMap
		Set<Entry<String, Integer>> mySet = seqCount.entrySet();
        List<Entry<String, Integer>> myList = new ArrayList<Entry<String, Integer>>(mySet);
        Collections.sort(myList, new Comparator<Map.Entry<String, Integer>>() 
        {
            public int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2) 
            {
            		// in ascending order
                return o1.getValue().compareTo(o2.getValue());
            }
        });

     // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : myList)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        for (Entry<String, Integer> entry : sortedMap.entrySet())
        {
        		writer.write(">" + entry.getValue() + "\n");
        		writer.write(entry.getKey() + "\n");
        }
		
		
		//close the writer
		writer.close();
		
	}

	// returns the header of this sequence without the “>”
	public String getHeader()
	{	
		return header;
	}

	// returns the DNA sequence of this FastaSequence
	public String getSequence() 
	{ 
		return sequence.toString();
	}
		
	// returns the number of G’s and C’s divided by the length of this sequence
	public float getGCRatio()
	{ 
		int countGC = 0;
		
		String currentSequence = this.getSequence().toUpperCase();
		
		for( int x=0; x < currentSequence.length(); x++ )
		{
			char target = currentSequence.charAt(x);
			
			if( target=='C' || target=='G' )
				countGC++;
		}
		
		return (float)countGC / currentSequence.length();
	}

	public static void main(String[] args) throws Exception
	{
		// ask user for the absolute path of a fasta file
		System.out.println("Please type the absolute path of your input fasta file");
		String filePath = System.console().readLine();
		File fileIn = new File(filePath);
		// ask user for the absolute path of a fasta file
		System.out.println("Please type the absolute path of your output fasta file");
		File fileOut = new File(System.console().readLine());
		
		// generate FastaSequence
		List<FastaSequence> fastaList = FastaSequence.readFastaFile(filePath);
		//Print out header-sequence
		for( FastaSequence fs : fastaList)
		{
		System.out.println(fs.getHeader());
		System.out.println(fs.getSequence());
		System.out.println(fs.getGCRatio());
		}
		// write sorted count-sequence in the ascending order of count into a file
		FastaSequence.writeUnique(fileIn, fileOut);
		
	}
}