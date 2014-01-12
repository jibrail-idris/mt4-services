package com.mt4agents.transformers;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTransformer<C, D> implements ITransformer<C, D> {
	public List<D> transformMany(List<C> cs) {
		List<D> ds = new ArrayList<D>();
		for (C c : cs) {
			ds.add(transform(c));
		}
		return ds;
	}
}
