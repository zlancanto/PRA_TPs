package mihan.sossou.crossword;

public class CrosswordSquare {
	
	private Character solution;
    private Character proposition;
    private String horizontal;
    private String vertical;
    // Case noire ou non
    private boolean statut;

    public CrosswordSquare() {
    	solution = ' ';
    	proposition = ' ';
    	horizontal = " ";
    	vertical = " ";
        statut = true;
    }  
    
    public Character getSolution() {
		return solution;
	}

	public void setSolution(Character solution) {
		this.solution = solution;
	}

	public Character getProposition() {
    	return proposition;
	}
    
    public void setProposition(Character proposition) {
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

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }
}