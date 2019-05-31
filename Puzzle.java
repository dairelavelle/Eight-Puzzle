//This is the code my group submitted for our intelligent systems project.
//The majority of the code was written or fixed by me.
//We intended to expand the code to be able to solve a fifteen puzzle but due to time restraints this wasn't implemented
//Therefore some methods require a puzzle size while others are hardcoded to the eight puzzle
import javax.swing.JOptionPane;
import java.util.*;
public class Puzzle {
    static Matrix startState= new Matrix();
    static Matrix endState= new Matrix();
    static Matrix currentState=new Matrix();
    static ArrayList<Matrix> closed=new ArrayList<Matrix>();
    static ArrayList<Matrix> open=new ArrayList<Matrix>();
    static int puzzleSize=0;

    public static void main(String [] args) {
        eightPuzzle();
    }

    //Opens a window and asks the user for an start state and then a window for the end state
    //If invalid input, it will keep asking until cancel is pressed
    static void eightPuzzle() {
    	startState=new Matrix(8);
    	endState=new Matrix(8);
    	currentState=new Matrix(8);
        String startStateIn = "";
        String endStateIn = "";
        
        boolean cancel=false;
        boolean valid=false;
        while (!valid && !cancel) {
            startStateIn = JOptionPane.showInputDialog(null, "Input a starting puzzle state");
            if(startStateIn == null)
            {
                cancel=true;
            }
            else {
            	valid = eighPuzzleValidation(startStateIn);
            	if (!valid)
                    JOptionPane.showMessageDialog(null, "ERROR:please input all numbers between 0-8 inclusive \nwithout duplicates");
            }
            
        }
        valid=false;
        while (!valid && !cancel) {
            endStateIn = JOptionPane.showInputDialog(null, "Input an ending puzzle state");
            if(endStateIn == null)
            {
                cancel=true;
            } else {
            	valid=eighPuzzleValidation(endStateIn);
            	if (!valid)
            		JOptionPane.showMessageDialog(null, "ERROR:please input all numbers between 0-8 inclusive \nwithout duplicates");
            }
        }
        String value [] = startStateIn.split(" ");
        String endValue [] = endStateIn.split(" ");
    	if(startStateIn.equals(endStateIn))
    	{
    		System.out.println(    value[0] + " " + value[1] + " " +  value[2] + "\n"
    						    +  value[3] + " " + value[4] + " " +  value[5] + "\n"
    						    +  value[6] + " " + value[7] + " " +  value[8] + "\n"
    						    +  "0 moves made states are identical");
    	} 
        else{
            int i=0;
            for (int j=0; j<3; j++) {
                for (int k=0;k<3;k++) {
                    startState.nodes[i]=new Node(Integer.parseInt(value[i]),j,k);
                    currentState.nodes[i]=new Node(Integer.parseInt(value[i]),j,k);
                    endState.nodes[i]=new Node(Integer.parseInt(endValue[i]),j,k);
                    i++;
                }
            }
            aStar();
        }
    }
    //First regex checks if the string is 17 characters long and all characters are between 0 and 8 inclusive
    //Second regex checks if there are any duplicates
    public static boolean eighPuzzleValidation(String in) {
	    boolean isValid=true;
		in=in.trim();
		if (!in.matches("([0-8 ]{17})+"))
			isValid=false;
		in=in.replaceAll(" ", "");
		if (!in.matches("^(?!.*(.).*\\1)\\d{9}"))
			isValid=false;
        return isValid;
    }
    //The statement in the loop checks how many moves away a node is in a given state from the desired end state
    //This is added to the total heuristic for each node and then the g value is added
    //The g value is how many moves it has taken for the given matrix to reach its state from the start state
    public static int calculateHeuristicValue(Matrix in)
    {
        int totalHeuristic=0;
        for (int i=0;i<in.getSize();i++) {
            totalHeuristic+=Math.abs(Math.abs(in.getNode(i).getPositionX()-endState.getNode(i).getPositionX())+Math.abs(in.getNode(i).getPositionY()-endState.getNode(i).getPositionY()));
        }
        return totalHeuristic+in.getG();
    }
    //This search algorithm was given to us in the project brief
    //First it checks which tiles can move into the 0 space and those are added to the open list
    //Then the start state is added to the closed list
    //The program then loops until it either finds the solution or finds out the puzzle is unsolvable
    //The lowest h value from the open list is selected. The list is not ordered by h value so it searches each time
    //The lowest h is removed from the open list and if it's the end state, the program finishes
    //Else the possible moves from that state are added to the open list if they are not already in the closed list
    //This is a min first traversal so runtime is very fast if it's solvable
    static void aStar() {
        //find moves for startstate
        int[] currentMoves=currentState.findZero(puzzleSize);
        for (int j=0; j<currentMoves.length; j++) {
            if (currentMoves[j]!=0) {
                open.add(Matrix.newMovedMatrix(currentState, currentMoves[j]));
            }
        }
        //add startstate to closed list
        closed.add(currentState);
        boolean finished=false;
        boolean foundInClosed;
        int min;
        ArrayList<Matrix> newStates=new ArrayList<Matrix>();
        int numberOfMoves=0;
        int rounds=0;
        int previousNumMoves=0;
        while (!finished && rounds<22000) {
            //find lowest f value in open
            min=10000;
            int remove=0;
            int j=0;
            for (; j<open.size(); j++) {
                if (calculateHeuristicValue(open.get(j))<min) {
                    currentState=open.get(j);
                    remove=j;
                    min=calculateHeuristicValue(open.get(j));
                }
            }
            open.remove(remove);
            //check if the puzzle is solved
            if (currentState.isEqual(endState)) {
                finished=true;
                System.out.printf("done after %d move(s).\nStart state:\n", numberOfMoves+1);
                finished();
            } else {
                //find possible moves for current state
                currentMoves=currentState.findZero(puzzleSize);
                for (int k=0; k<currentMoves.length; k++) {
                    if (currentMoves[k]!=0) {
                        newStates.add(Matrix.newMovedMatrix(currentState, currentMoves[k]));
                    }
                }
                //check if current state is in closed list
                for (int k=0; k<newStates.size(); k++) {
                    foundInClosed=false;
                    for (int l=0; l<closed.size() && !foundInClosed; l++) {
                        if (newStates.get(k).isEqual(closed.get(l))){
                            foundInClosed=true;
                        }
                    }
                    if (!foundInClosed) {
                        open.add(newStates.get(k));
                    }
                }
                newStates.clear();
            }
            numberOfMoves=currentState.getG();
            if(numberOfMoves>previousNumMoves) {
                previousNumMoves=numberOfMoves;
            }
            closed.add(new Matrix(currentState));
            rounds++;
        }
        if (rounds>=22000) {
            System.out.println("Unsolvable");
        }
    }
    //Follows the lineage from the solved state to the start state through the shortest path then prints them to the console
    static void finished() {
        Matrix check=currentState;
        ArrayList<Matrix> shortestPath=new ArrayList<Matrix>();
        while (check!=null) {
            shortestPath.add(check);
            check=check.getParent();
        }
        for (int i=shortestPath.size()-1; i>=0; i--) {
            System.out.println(shortestPath.get(i));   
        }
    }
}
// ****************************************
//              Private classes
//*****************************************

class Node{
    int val; //numeric value
    int posx; //Is it in the specified row position
    int posy; //Is it in the specified coloumn position

    public Node(int value, int positionX, int positionY)
    {
        val = value;
        posx = positionX;
        posy = positionY;
    }

    public int getValue() 	  {return val;}
    public int getPositionX() {return posx;}
    public int getPositionY() {return posy;}

    public void setValue(int value) 	   {val = value;}
    public void setPositionX(int position) {posx = position;}
    public void setPositionY(int position) {posy = position;}

    //Checks if the zero node can move or if it is against a side
    //returns false for moves that can't be made
    //the array is in {left, up, right, down}
    public boolean[] findMoves(int puzzleSize) {
    	int width=(int)Math.sqrt(puzzleSize+1)-1;
    	boolean[] out={true, true, true, true};
    	if (posy<1)
    		out[0]=false;
    	if (posx<1)
    		out[1]=false;
    	if (posy>width-1)
    		out[2]=false;
    	if (posx>width-1)
    		out[3]=false;
    	return out;
    }
}

class Matrix {
    Matrix parent;
    private int g=0; //gn from the brief
    private int size=0;
    private int width=0;
    public 	Node nodes[]=new Node[9];
    //constructors
    public Matrix(){}
    //creates an empty matrix for an 8 or 15 puzzle
    public Matrix(int squares) { 
        size=squares+1;
    	nodes=new Node[size];
    	width=(int)Math.sqrt(size);
    }
    //creates a new matrix that's a deep copy of a given matrix
    public Matrix(Matrix in) {
        nodes=new Node[in.nodes.length];
        for (int i=0; i<nodes.length; i++) {
            this.nodes[i]=new Node(in.nodes[i].getValue(),in.nodes[i].getPositionX(),in.nodes[i].getPositionY());
        }
        size=in.getSize();
        width=in.getWidth();
        g=in.getG();
    }
    //getters and setters
    //No getter or setter for nodes
    //just use Mastrix.nodes[], for example startState.nodes[1]
    public int 	getSize() {return size;}
    public int 	getG() {return g;}
    public int  getWidth() {return width;}
    public Matrix getParent() {return parent;}
    public void setParent(Matrix in) {parent=in;}
    public Node getNode(int in) {
    	boolean found=false;
    	for(int i=0; i<size && !found; i++) {
    		if (nodes[i].getValue()==in) {
    			return nodes[i];
    		}
    	}
    	return nodes[0];
    }

    //finds the position in the nodes array of the node with the same value as the argument
    public int findNode(int in) {
    	for(int i=0; i<nodes.length; i++) {
    		if (in==nodes[i].getValue())
    			return i;
    	}
    	return 0;
    }

    public void setG(int in) {g=in;}
    public void plusG() {g++;}

    //Checks if all the nodes in another Matrix have the same positions 
    boolean isEqual(Matrix in) {
    	boolean out=true;
    	for (int j=0; j<nodes.length && out; j++) {
			if (this.nodes[j].getValue()!=in.nodes[j].getValue())
				out=false;
		}
		return out;
    }

    //formats the Matrix into a string of a 3x3 or 4x4 grid
    @Override
    public String toString() {
    	String out="";
    	int index=0;
    	for(int i=0; i<width;i++) {
    		for (int j=0;j<width;j++) {
    			out+=nodes[index].getValue()+" ";
    			index++;
    		}
    		out+="\n";
    	}
    	return out;
    }

    //Takes an argument of 8 or 15
    //Returns an array of numbers that can move into 0
    //The array is arranged like so: [left of 0, above 0, right of 0, under 0]
    public int[] findZero(int puzzleSize) {
    	boolean found=false;
    	int[] out=new int[4];
    	for(int i=0; i<size && !found; i++) {
    		if (nodes[i].getValue()==0) {
    			found=true;
    			boolean[] moves=nodes[i].findMoves(puzzleSize);
    			//System.out.println("Possible moves: ");
				if (moves[0]==true) {
					// System.out.println(nodes[i-1].getValue()+" to the east");
					out[0]=nodes[i-1].getValue();
				}
				if (moves[1]==true) {
					// System.out.println(nodes[i-width].getValue()+" to the south");
					out[1]=nodes[i-width].getValue();
				}

				if (moves[2]==true) {
					// System.out.println(nodes[i+1].getValue()+" to the west");
					out[2]=nodes[i+1].getValue();
				}
				if (moves[3]==true) {
					// System.out.println(nodes[i+width].getValue()+" to the north");
					out[3]=nodes[i+width].getValue();
				}
    		}
    	}
    	return out;
    }
    //Creates a new Matrix based on the old with the position of 0 and the argument swapped
    //The new Matrix's parent points to the old. This way there's a trail of all moves back to the start state
    public static Matrix newMovedMatrix(Matrix old, int in) {
    	int zeroLocation=old.findNode(0);
    	int inLocation=old.findNode(in);
    	Node tempZero=new Node(0,old.getNode(in).getPositionX(),old.getNode(in).getPositionY());
    	Node tempIn=new Node(in,old.getNode(0).getPositionX(),old.getNode(0).getPositionY());
    	Matrix out=new Matrix(old.getSize()-1); //HARDCODED
        out.setParent(old);
    	for (int i=0; i<old.getSize(); i++) {
    		if (i!=zeroLocation && i!=inLocation)
    			out.nodes[i]=new Node(old.nodes[i].getValue(), old.nodes[i].getPositionX(), old.nodes[i].getPositionY());
    		else if (i==zeroLocation)
    			out.nodes[i]=tempIn;
    		else
    			out.nodes[i]=tempZero;
    	}
        out.setG(old.getG());
        out.plusG();
    	return out;
    }
}