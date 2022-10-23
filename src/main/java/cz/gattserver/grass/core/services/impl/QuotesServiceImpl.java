package cz.gattserver.grass.core.services.impl;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass.core.interfaces.QuoteTO;
import cz.gattserver.grass.core.services.CoreMapperService;
import cz.gattserver.grass.core.services.QuotesService;
import cz.gattserver.grass.core.services.RandomSourceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass.core.model.domain.Quote;
import cz.gattserver.grass.core.model.repositories.QuoteRepository;

@Transactional
@Service
public class QuotesServiceImpl implements QuotesService {

	@Autowired
	private CoreMapperService mapper;

	@Autowired
	private QuoteRepository quoteRepository;

	@Autowired
	private RandomSourceService randomSourceService;

	@Override
	public long createQuote(String content) {
		Validate.notBlank(content, "Obsah hlášky nesmí být prázdný");
		Quote quote = new Quote();
		quote.setName(content);
		quote = quoteRepository.save(quote);
		return quote.getId();
	}

	@Override
	public void modifyQuote(long quoteId, String modifiedContent) {
		Validate.notBlank(modifiedContent, "Obsah hlášky nesmí být prázdný");
		Quote quote = new Quote();
		quote.setId(quoteId);
		quote.setName(modifiedContent);
		quoteRepository.save(quote);
	}

	@Override
	public List<QuoteTO> getQuotes(String filter) {
		List<Quote> quotes = StringUtils.isBlank(filter) ? quoteRepository.findAll()
				: quoteRepository.findLike("%" + filter.toLowerCase() + "%");
		List<QuoteTO> quoteDTOs = new ArrayList<>();
		for (Quote quote : quotes)
			quoteDTOs.add(mapper.map(quote));
		return quoteDTOs;
	}

	@Override
	public String getRandomQuote() {
		long count = quoteRepository.count();
		if (count == 0)
			return "";
		return quoteRepository.findRandom(randomSourceService.getRandomLong(count));
	}

	@Override
	public void deleteQuote(long quoteId) {
		quoteRepository.deleteById(quoteId);
	}

}
