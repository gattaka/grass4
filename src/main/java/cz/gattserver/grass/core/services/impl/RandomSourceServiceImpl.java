package cz.gattserver.grass.core.services.impl;

import java.util.Random;

import cz.gattserver.grass.core.services.RandomSourceService;
import org.springframework.stereotype.Service;

@Service("randomSourceServiceImpl")
public class RandomSourceServiceImpl implements RandomSourceService {

	@Override
	public long getRandomLong(long range) {
		return (long) Math.floor(Math.random() * range);
	}

	@Override
	public int getRandomInt(int range) {
		if (range == 0)
			return 0;
		return new Random().nextInt(range);
	}

}
