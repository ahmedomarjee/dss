/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.tsl.cache.access;

import eu.europa.esig.dss.tsl.cache.CacheKey;
import eu.europa.esig.dss.tsl.cache.DownloadCache;
import eu.europa.esig.dss.tsl.cache.ParsingCache;
import eu.europa.esig.dss.tsl.cache.ValidationCache;
import eu.europa.esig.dss.tsl.download.XmlDownloadResult;
import eu.europa.esig.dss.tsl.parsing.AbstractParsingResult;
import eu.europa.esig.dss.tsl.validation.ValidationResult;

public class CacheAccessByKey extends ReadOnlyCacheAccessByKey {
	
	public CacheAccessByKey(final CacheKey key, final DownloadCache downloadCache, final ParsingCache parsingCache,
			final ValidationCache validationCache) {
		super(key, downloadCache, parsingCache, validationCache);
	}
	
	/**
	 * Returns the CacheKey
	 * @return {@link CacheKey}
	 */
	public CacheKey getCacheKey() {
		return key;
	}

	public boolean isUpToDate(XmlDownloadResult xmlDownloadResult) {
		return downloadCache.isUpToDate(key, xmlDownloadResult);
	} 

	public void update(XmlDownloadResult result) {
		downloadCache.update(key, result);
	}

	public void downloadError(Exception e) {
		downloadCache.error(key, e);
	}

	public boolean isParsingRefreshNeeded() {
		return parsingCache.isRefreshNeeded(key);
	}

	public void update(AbstractParsingResult parsingResult) {
		parsingCache.update(key, parsingResult);
	}

	public void expireParsing() {
		parsingCache.expire(key);
	}

	public void parsingError(Exception e) {
		parsingCache.error(key, e);
	}

	public boolean isValidationRefreshNeeded() {
		return validationCache.isRefreshNeeded(key);
	}

	public void expireValidation() {
		validationCache.expire(key);
	}

	public void update(ValidationResult validationResult) {
		validationCache.update(key, validationResult);
	}

	public void validationError(Exception e) {
		validationCache.error(key, e);
	}
	
	/**
	 * Checks if the entry must be deleted from the file cache (download cache)
	 * @return TRUE if the entry need to be deleted, FALSE otherwise
	 */
	public boolean isFileNeedToBeDeleted() {
		return downloadCache.isToBeDeleted(key);
	}
	
	/**
	 * Removes the entry from downloadCache if its value is TO_BE_DELETED
	 */
	public void deleteDownloadCacheIfNeeded() {
		if (downloadCache.isToBeDeleted(key)) {
			downloadCache.remove(key);
		}
	}
	
	/**
	 * Removes the entry from parsingCache if its value is TO_BE_DELETED
	 */
	public void deleteParsingCacheIfNeeded() {
		if (parsingCache.isToBeDeleted(key)) {
			parsingCache.remove(key);
		}
	}
	
	/**
	 * Removes the entry from parsingCache if its value is TO_BE_DELETED
	 */
	public void deleteValidationCacheIfNeeded() {
		if (validationCache.isToBeDeleted(key)) {
			validationCache.remove(key);
		}
	}

}
