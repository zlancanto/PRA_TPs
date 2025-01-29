package mihan.sossou.tp2;

public class CrosswordSquare {
	
	private Character solution;
    private Character proposition;
    private String horizontal;
    private String vertical;
    private boolean status;
    
    public CrosswordSquare() {
    	this.status = false;
	}
    
    public Character getSolution() {
    	return solution;
	}
    
    public void setSolution( Character solution) {
		this.solution = solution;
	}
    
    public Character getPropCharacter() {
    	return proposition;
	}
    
    public void setPropostion(Character proposition) {
		this.proposition = proposition;
	}
    
    public String getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(String horizontal) {
        this.horizontal = horizontal;
    }

    public String getVertical() {
        return vertical;
    }

    public void setVertical(String vertical) {
        this.vertical = vertical;
    }
    
    public boolean status() {
		return status;
	}
    
    public void setBlack( boolean black) {
		status = black;
	}

}
