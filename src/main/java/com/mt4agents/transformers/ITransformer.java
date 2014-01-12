package com.mt4agents.transformers;

import java.util.List;

public interface ITransformer<C,D> {
	public List<D> transformMany(List<C> cs);
	public D transform(C c);
}
