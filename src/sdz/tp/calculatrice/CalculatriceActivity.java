package sdz.tp.calculatrice;

import sdz.tp.calculatrice.OperateurButton.Operateurs;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class CalculatriceActivity extends Activity {
	public enum States
	{
		S0, S1, S2, S3 
	};
	
	private States actuel = States.S0;
	
	private String affichage = "0";
	private TextView affichageView = null;
	
	private StringBuffer a = null;
	private Operateurs op = null;
	private StringBuffer b = null;
	
	private RadioGroup unite = null;
	private OnCheckedChangeListener uniteListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(actuel == States.S0 || actuel == States.S3)
			{
				float newValue = 0;
				switch(checkedId)
				{
				case R.widget.degres:
					//Si on passe en degrés, on convertit "a" en degrés
					newValue = (float) Math.toDegrees(Double.parseDouble(affichage));
					break;
					
				case R.widget.radians:
					//Si on passe en radians, on convertit "a" en radians
					newValue = (float) Math.toRadians(Double.parseDouble(affichage));
					break;
				}
				//On passe en S3
				setActuel(States.S3);
				//On met à jour l'affichage
				a = new StringBuffer(String.valueOf(newValue));
				updateAffichage();
			}
		}
	};
	
	private Button clear = null;
	private View.OnClickListener clearListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//On passe à S0
			setActuel(States.S0);
			//Et on met "a" à 0
			setA(0);
		}
	};
	
	private Button retour = null;
	private View.OnClickListener retourListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(actuel)
			{
			case S3:
				//si on se trouve en S3, on retourne en S0
				setActuel(States.S0);
			case S0:
				//On efface le dernier chiffre de "a"
				applyRetourArriereA();
				break;
			case S1:
				//On enlève l'opérateur actuel
				op = null;
				//On retour à l'état précédent
				actuel = States.S0;
				updateAffichage();
			case S2:
				//On efface le dernier chiffre de "b"
				applyRetourArriereB();
				break;
			}
		}
	};
	
	private Button off = null;
	private View.OnClickListener offListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//Permet d'éteindre l'application
			finish();
		}
	};
	
	private TrigoButton trigo = null;
	private CheckBox shift = null;
	private CompoundButton.OnCheckedChangeListener shiftListener = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			//On indique au bouton de trigonométrie qu'on a changé d'état
			trigo.switchState();
		}
	};
	
	private Button point = null;
	private View.OnClickListener pointListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//Si on est en S0
			if(actuel == States.S0)
			{
				if(a == null)
					a = new StringBuffer("0.");
				//S'il n'y a pas de point déjà
				else if(a.lastIndexOf(".") == -1)
					a.append(".");
				//si on est en édition de "b" (S2)
			}else if(actuel == States.S2)
			{
				if(b == null)
					b = new StringBuffer("0.");
				//S'il n'y a pas de point déjà
				else if(b.lastIndexOf(".") == -1)
					b.append(".");
			}
			updateAffichage();
		}
	};
	
	private Button egal = null;
	private View.OnClickListener egalListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//Ne fonctionne qu'après avoir effectué une opération complète 
			if(actuel == States.S2)
			{
				float result = 0.0f;
				//on sculpte correctement "b", puisque "a" l'est déjà
				formatNumber(b);
				try
				{
					switch(op)
					{
					case add:
						result = Float.parseFloat(a.toString()) + Float.parseFloat(b.toString());
						break;
					case sou:
						result = Float.parseFloat(a.toString()) - Float.parseFloat(b.toString());
						break;
					case mul:
						result = Float.parseFloat(a.toString()) * Float.parseFloat(b.toString());
						break;
					case div:
						float diviseur = Float.parseFloat(b.toString());
						//Si le diviseur vaut 0, on envoit un Toast
						if(diviseur == 0)
							throw new ArithmeticException();
						result = Float.parseFloat(a.toString()) / diviseur;
						break;
					}
					b = null;
					setActuel(States.S3);
					//Si le résultat n'est pas un entier...
					if((int)result != result)
						setA(result);
					else
						//Sinon
						setA((int)result);
				}catch(ArithmeticException e)
				{
					Toast.makeText(CalculatriceActivity.this, "Division par 0 !", Toast.LENGTH_SHORT).show();
					setActuel(States.S2);
				}
			}
		}
	};
	
	private Button sign = null;
	private View.OnClickListener signListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if((actuel == States.S0 || actuel == States.S3) && a != null)
			{
				float value = Float.parseFloat(a.toString());
				if(value != 0)
					//Si la valeur est > à 0, on met un "-" devant
					if(value > 0)
						a.insert(0, "-");
					else
						//Sinon on supprime le "-"
						a.deleteCharAt(0);
				updateAffichage();
			}else if(actuel == States.S2 && b != null)
			{
				float value = Float.parseFloat(b.toString());
				if(value != 0)
					if(value > 0)
						//Si la valeur est > à 0, on met un "-" devant
						b.insert(0, "-");
					else
						//Sinon on supprime le "-"
						b.deleteCharAt(0);
				updateAffichage();
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        unite = (RadioGroup) findViewById(R.id.radioGroup);
        unite.setOnCheckedChangeListener(uniteListener);
        
        clear = (Button) findViewById(R.bouton.clear);
        clear.setOnClickListener(clearListener);
        
        retour = (Button) findViewById(R.bouton.arr);
        retour.setOnClickListener(retourListener);
        
        off = (Button) findViewById(R.bouton.off);
        off.setOnClickListener(offListener);
        
        shift = (CheckBox) findViewById(R.checkBox.shift);
        shift.setOnCheckedChangeListener(shiftListener);
        
        trigo = (TrigoButton) findViewById(R.bouton.trigo);
        
        point = (Button) findViewById(R.bouton.point);
        point.setOnClickListener(pointListener);
        
        egal = (Button) findViewById(R.bouton.boutonEgal);
        egal.setOnClickListener(egalListener);
        
        sign = (Button) findViewById(R.bouton.signChange);
        sign.setOnClickListener(signListener);
        
        affichageView = (TextView) findViewById(R.widget.affichage);
    }
    
    /**
     * 
     * @return true si on est en mode "degrés"
     */
    public boolean isDegrees()
    {
    	return unite.getCheckedRadioButtonId() == R.widget.degres;
    }
    
    /**
     * Ajoute un chiffre à la fin de "a"
     * @param value le chiffre à ajouter
     */
    public void appendToA(int value)
    {	
    	if(actuel == States.S0 || actuel == States.S3)
    	{
    		//Si "a" est null ou vaut 0, on lui met directement "value"
	    	if(a == null || a.toString().compareTo("0") == 0)
	    		setA(value);
	    	else
        		//sinon on l'ajoute à la fin de "a"
	    		a.append(String.valueOf(value));
	    	updateAffichage();
    	}
    }
    
    /**
     * Ajoute un chiffre à la fin de "b"
     * @param value le chiffre à ajouter
     */
    public void appendToB(int value)
    {
    	if(actuel == States.S1 || actuel == States.S2)
    	{
    		//Si "b" est null ou vaut 0, on lui met directement "value"
    		if(b == null || b.toString().compareTo("0") == 0)
        		setB(value);
        	else
        		//sinon on l'ajoute à la fin de "b"
        		b.append(String.valueOf(value));
        	updateAffichage();
    	}
    }
    
    /**
     * Réaction en cas d'appui sur le bouton "retour arrière" sur "a"
     */
    private void applyRetourArriereA()
    {
    	if((actuel == States.S0 || actuel == States.S3) && a != null)
    	{
    		//Si "a" a plus d'un chiffre, alors on retire le dernier chiffre
			if(Float.parseFloat(a.toString()) >= 10.0f || Float.parseFloat(a.toString()) <= -10.0f)
				a.deleteCharAt(a.length() - 1);
			else
				//sinon on lui met "0"
				a = new StringBuffer("0");
			updateAffichage();
    	}
    }
    
    /**
     * Réaction en cas d'appui sur le bouton "retour arrière" sur "b"
     */
    private void applyRetourArriereB()
    {
    	if(actuel == States.S2 && b != null)
    	{
    		//Si "b" a plus d'un chiffre, alors on retire le dernier chiffre
			if(Float.parseFloat(b.toString()) >= 10.0f || Float.parseFloat(b.toString()) <= -10.0f)
				b.deleteCharAt(b.length() - 1);
			else
			{
				//On met b à null
				b = null;
				//On retourne à l'étape précédente
				actuel = States.S1;
			}
	    	updateAffichage();
    	}
    }
    
    /**
     * Formate une chaîne de caractère en un nombre valable
     * @param number le nombre à formater
     */
    private void formatNumber(StringBuffer number)
    {
    	//Enlève le "." final s'il n'y a rien après
    	if(number.charAt(number.length() - 1) == '.')
    		number.deleteCharAt(number.length() - 1);
    }
    
    /**
     * 
     * @param value nouvelle valeur de "a"
     */
    public void setA(String value)
    {
    	if(actuel == States.S0 || actuel == States.S3)
    	{
    		this.a = new StringBuffer(value);
    		updateAffichage();
    	}
    }
    
    public void setA(int value)
    {
    	setA(String.valueOf(value));
    }
    
    public void setA(float value)
    {
    	setA(String.valueOf(value));
    }

    /**
     * @return la valeur de "a" ou 0.0f s'il est nul
     */
    public float getA()
    {
    	if(a != null)
    		return Float.parseFloat(a.toString());
    	return 0.0f;
    }
    
    /**
     * @return la valeur de "b" ou 0.0f s'il est nul
     */
    public void setB(int value)
    {
    	if(actuel == States.S2)
    	{
        	this.b = new StringBuffer(String.valueOf(value));
        	updateAffichage();
    	}
    }
    
    /**
     * Change l'opérateur actuel
     * @param op le caractère de l'opération (+, -, * ou /)
     */
    public void setOp(char op)
    {
    	//On formate le "a" pour qu'il soit cohérent
    	formatNumber(a);
    	//On passe au deuxième état
    	setActuel(States.S1);
    	//On récupère l'opérateur depuis le caractère
    	this.op = Operateurs.fromChar(op);
    	//On met à jour l'affichage
    	updateAffichage();
    }

	/**
	 * Change l'état actuel
	 * @param nouveau le nouvel état
	 */
	public void setActuel(States nouveau) {
		//On vérifie qu'on ne fait pas un saut interdit
		if(!(this.actuel == States.S2 && nouveau == States.S0))
			this.actuel = nouveau;
	}
	
	/**
	 * Met à jour l'affichage en fonction de l'état actuel
	 */
	public void updateAffichage()
	{
		switch(actuel)
		{
		case S0:
		case S3:
			updateAffichageS0();
			break;
		
		case S1:
			updateAffichageS1();
			break;
			
		case S2:
			updateAffichageS2();
			break;
		}
	}

	/**
	 * Si on se trouve en S0 ou S3
	 */
	private void updateAffichageS0() {
		//On se contente d'afficher le "a"
		this.affichage = a.toString();
		affichageView.setText(a);
	}
	
	/**
	 * Si on se trouve en S1
	 */
	private void updateAffichageS1() {
		//On se contente d'afficher le "a" et l'opérateur
		this.affichage = a.toString().concat(" " + op.toString() + " ");
		affichageView.setText(affichage);
	}
	
	/**
	 * Si on se trouve en S2
	 */
	private void updateAffichageS2() {
		//On affiche le "a", l'opérateur et le "b"
		this.affichage = a.toString().concat(" " + op.toString() + " ").concat(b.toString());
		affichageView.setText(affichage);
	}
    
	public States getActuel() {
		return actuel;
	}

	public String getAffichage() {
		return affichage;
	}
}