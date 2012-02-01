package sdz.tp.calculatrice;

import sdz.tp.calculatrice.CalculatriceActivity.States;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class TrigoButton extends Button implements View.OnClickListener, View.OnLongClickListener{
	private CalculatriceActivity activity;
	private boolean stateOne = true;
	
	public TrigoButton(Context context) {
		super(context);

		init(context);
	}
	
	public TrigoButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}

	public TrigoButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init(context);
	}
	
	private void init(Context c)
	{
		activity = (CalculatriceActivity) c;
		setOnClickListener(this);
		setOnLongClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(activity.getActuel() == States.S0 || activity.getActuel() == States.S3)
		{
			float tmp = activity.getA();
			//On passe en S3
			activity.setActuel(States.S3);
			//Si on est en degrés
			if(activity.isDegrees())
				//On convertit en radians (les opérateurs de trigonométrie de Java opèrent toujours en radians)
				tmp = (float) Math.toRadians(tmp);
			//En mode normal
			if(stateOne)
				activity.setA((float)Math.cos(tmp));
			else
				//En mode SHIFT
				activity.setA((float)Math.sin(tmp));
		}
		
	}
	
	/**
	 * Permet de changer d'état du bouton
	 */
	public void switchState()
	{
		stateOne = !stateOne;
	}

	@Override
	public boolean onLongClick(View v) {
		switchState();
		onClick(this);
		switchState();
		return true;
	}

}
