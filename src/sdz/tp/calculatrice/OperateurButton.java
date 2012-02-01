package sdz.tp.calculatrice;

import sdz.tp.calculatrice.CalculatriceActivity.States;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class OperateurButton extends Button implements View.OnClickListener{
	public enum Operateurs {
		//Chaque énumérateur a une valeur en caractère
		  add('+'),
		  sou('-'),
		  mul('*'),
		  div('/');

		  private char car;

		  private Operateurs(char car) {
		    this.car = car;
		  }

		  public char getCar() {
		    return this.car;
		  }
		  
		  public String toString()
		  {
			  return String.valueOf(this.car);
		  }
		  
		  //Retourne un Operateur en fonction d'une chaîne de caractère
		  public static Operateurs fromString(String text) {
			  return fromChar(text.charAt(0));
		  }

		//Retourne un Opérateur en fonction d'un caractère
		  public static Operateurs fromChar(char text) {
			  if (text != ' ') {
				  for (Operateurs b : Operateurs.values()) {
					  if (text == b.car) {
						  return b;
					  }
				  }
			  }
			  return null;
		  }
	}
	
	private Operateurs op;
	private CalculatriceActivity activity;
	
	public OperateurButton(Context context) {
		super(context);
		init(context);
	}
	
	public OperateurButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public OperateurButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context c)
	{
		activity = (CalculatriceActivity) c;
		setOp(Operateurs.fromString(getText().toString()));
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if(activity.getActuel() != States.S2)
			activity.setOp(op.getCar());
	}

	public Operateurs getOp() {
		return op;
	}

	public void setOp(Operateurs op) {
		this.op = op;
	}

}
