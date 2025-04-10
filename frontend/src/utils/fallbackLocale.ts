/**
 * Fallback utility to fetch locale files if the import method doesn't work
 */

// Define type for locale data
export interface LocaleMessages {
  [key: string]: LocaleMessages | string;
}

// Default locale to fall back to if fetch fails
const DEFAULT_LOCALE_DATA: LocaleMessages = {
  home: {
    welcome: "Welcome to Clozet!",
    tagline: "The new way to shop for clothes",
    searchPlaceholder: "Search for a product...",
    createPost: "Create a post!",
    popularCategories: "Popular Categories",
    loading: "Loading...",
    pleaseLogin: "Please login to create a post"
  }
};

/**
 * Fetches a locale file from the server
 * @param {string} locale - The locale code (e.g. 'en', 'nb', 'es')
 * @returns {Promise<LocaleMessages>} - The locale data
 */
export async function fetchLocale(locale: string): Promise<LocaleMessages> {
  try {
    // First try the /locales path (for production)
    const response = await fetch(`/locales/${locale}.json?t=${new Date().getTime()}`);

    if (response.ok) {
      return await response.json() as LocaleMessages;
    }

    // If that fails, try the /src/locales path (for development)
    const fallbackResponse = await fetch(`/src/locales/${locale}.json?t=${new Date().getTime()}`);

    if (fallbackResponse.ok) {
      return await fallbackResponse.json() as LocaleMessages;
    }

    // If all fetches fail, use hardcoded defaults
    console.error(`Failed to load locale: ${locale}, using defaults`);
    return DEFAULT_LOCALE_DATA;
  } catch (error) {
    console.error(`Error loading locale: ${locale}`, error);
    return DEFAULT_LOCALE_DATA;
  }
}
