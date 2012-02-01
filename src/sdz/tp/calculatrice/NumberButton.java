package sdz.tp.calculatrice;

import sdz.tp.calculatrice.CalculatriceActivity.States;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class NumberButton extends Button implements View.OnClickListener{
	private int value;
	private CalculatriceActivity activity = null;
	
	public NumberButton(Context context) {
		super(context);
		init(context);
	}
	
	public NumberButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NumberButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context)
	{
		activity = (CalculatriceActivity) context;
		value = Integer.parseInt(getText().toString());
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		States actuel = activity.getActuel();
		switch (actuel) {
		case S0:
			activity.appendToA(value);
			break;
		case S1:
			activity.setActuel(States.S2);
		case S2:
			activity.appendToB(value);
			break;
		case S3:
			activity.setActuel(States.S0);
			activity.setA(value);
		default:
			break;
		}
	}
	
	public int getValue() {
		return value;
	}

	public CalculatriceActivity getActivity() {
		return activity;
	}

	public void setActivity(CalculatriceActivity activity) {
		this.activity = activity;
	}

}
