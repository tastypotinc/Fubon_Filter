package org.Fubon.Server.Analysis;

import org.Fubon.Server.DataStore.Stock;
import org.Fubon.Server.Signal;

public interface Rule {
	public void SetStock(Stock pstock);
	public boolean RuleChecking();
	public boolean generateSignal();
	public void init(Stock pstock);
	//public String Signal();
	public Signal Signal();
	public boolean isValid();
	public String RuleDumping();
}
