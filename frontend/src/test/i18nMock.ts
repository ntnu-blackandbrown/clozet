import { createI18n, type I18n } from 'vue-i18n'

/**
 * Creates a mocked i18n instance for testing
 * @param customMessages Optional custom messages to override defaults
 * @returns A configured i18n instance
 */
export function createMockI18n(customMessages = {}): I18n {
  const defaultMessages = {
    en: {
      navigation: {
        home: 'Home',
        profile: 'Profile',
        messages: 'Messages',
        sellItems: 'Sell Items',
        adminDashboard: 'Admin Dashboard',
      },
      common: {
        skipToContent: 'Skip to main content',
        toggleMenu: 'Toggle menu',
        logout: 'Log out',
        login: 'Log in',
        register: 'Register',
        searchPlaceholder: 'Search...',
        cancel: 'Cancel',
        save: 'Save',
        delete: 'Delete',
        edit: 'Edit',
        close: 'Close',
      },
      categories: {
        title: 'Categories',
        manage: 'Manage Categories',
        new: 'New Category',
        edit: 'Edit Category',
        delete: 'Delete Category',
        name: 'Category Name',
        description: 'Description',
      },
      auth: {
        login: 'Log in',
        register: 'Register',
        email: 'Email',
        password: 'Password',
        confirmPassword: 'Confirm Password',
        forgotPassword: 'Forgot Password?',
        resetPassword: 'Reset Password',
        username: 'Username',
      },
      validation: {
        required: 'This field is required',
        email: 'Please enter a valid email',
        passwordLength: 'Password must be at least 8 characters',
        passwordMatch: 'Passwords do not match',
      },
      errors: {
        generic: 'An error occurred',
        unauthorized: 'You are not authorized',
        notFound: 'Not found',
      }
    }
  }

  // Merge custom messages with defaults
  const messages = {
    en: {
      ...defaultMessages.en,
      ...(customMessages.en || {})
    }
  }

  return createI18n({
    legacy: false,
    locale: 'en',
    messages
  })
}
