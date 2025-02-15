///
/// Copyright OmniFaces
///
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
/// the License. You may obtain a copy of the License at
///
///     https://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
/// an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
/// specific language governing permissions and limitations under the License.
///

import { Util } from "./Util";

/**
 * Lazy image loader.
 * 
 * @author Bauke Scholtz
 * @see org.omnifaces.component.output.GraphicImage
 * @see https://css-tricks.com/the-complete-guide-to-lazy-loading-images/
 * @since 3.10
 */
export module GraphicImage {

	// Private static functions ---------------------------------------------------------------------------------------

	/**
	 * Load lazy images rendered by o:graphicImage.
	 */
	function initLazyImages() {
		const lazyImages = getLazyImages();
		
		if (lazyImages.length == 0) {
			return;
		}

		if (window.IntersectionObserver) { // https://caniuse.com/?search=IntersectionObserver
			const lazyImageObserver = new IntersectionObserver(function(entries) {
				entries.forEach(function(entry) {
					if (entry.isIntersecting && entry.target instanceof HTMLImageElement) {
						const img = entry.target;
						loadLazyImage(img);
						lazyImageObserver.unobserve(img);
					}
				});
			});

			lazyImages.forEach(function(img) {
				lazyImageObserver.observe(img);
			});
		}
		else {
			let lazyImageListenerThrottle: number;
			const lazyImageListener = function() {
				if (lazyImageListenerThrottle) {
					clearTimeout(lazyImageListenerThrottle); // Throttle is used because firing of resize and scroll events can get quite intense.
				}

				lazyImageListenerThrottle = setTimeout(function() {
					const lazyImages = getLazyImages();
					const offsetTopThreshold = window.innerHeight + window.pageYOffset;

					for (var i = 0; i < lazyImages.length; i++) {
						const img = lazyImages[i];

						if (img.offsetTop < offsetTopThreshold) {
							loadLazyImage(img);
						}
					}

					if (getLazyImages().length == 0) {
						Util.removeEventListener(window, "resize orientationChange", lazyImageListener);
						Util.removeEventListener(document, "scroll", lazyImageListener);
					}
				}, 50);
			}

			Util.addEventListener(window, "resize orientationChange", lazyImageListener);
			Util.addEventListener(document, "scroll", lazyImageListener);
		}
	}

	/**
	 * Returns all lazy images generated by o:graphicImage which still need loading.
	 */
	function getLazyImages(): NodeListOf<HTMLImageElement> {
		return document.querySelectorAll("img[src][data-src][data-lazy]");
	}

	/**
	 * Basically sets data-src to src.
	 */
	function loadLazyImage(img: HTMLImageElement) {
		const data = img.dataset;

		if (data.lazy && data.src) {
			img.src = data.src;
		}

		delete data.src;
		delete data.lazy;
	}

	// Global initialization ------------------------------------------------------------------------------------------

	Util.addOnloadListener(initLazyImages);

}