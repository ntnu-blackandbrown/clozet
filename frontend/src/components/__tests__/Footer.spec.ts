import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createI18n } from 'vue-i18n'
import { createRouter, createWebHistory } from 'vue-router'
import Footer from '@/components/layout/Footer.vue'

// We mock translations here for demonstration;
// adjust/expand them to match your actual i18n setup
const i18n = createI18n({
  locale: 'en',
  messages: {
    en: {
      footer: {
        about: {
          title: 'About Us',
          description: 'Some description about the company.',
        },
        social: {
          followInstagram: 'Follow us on Instagram',
          instagram: 'Instagram',
          followFacebook: 'Follow us on Facebook',
          facebook: 'Facebook',
          followTwitter: 'Follow us on Twitter',
          twitter: 'Twitter',
        },
        quickLinks: 'Quick Links',
        helpSupport: 'Help & Support',
        links: {
          faq: 'FAQ',
          contactUs: 'Contact Us',
          termsOfService: 'Terms of Service',
          privacyPolicy: 'Privacy Policy',
        },
        account: 'Account',
      },
      navigation: {
        profile: 'Profile',
        messages: 'Messages',
        sellItems: 'Sell Items',
      },
      common: {
        login: 'Log In',
        register: 'Register',
      },
    },
  },
})

describe('Footer.vue', () => {
  let router: ReturnType<typeof createRouter>

  beforeEach(() => {
    // Optional: clear mocks, etc.
    vi.clearAllMocks()

    // Create a minimal router setup
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/profile', name: 'Profile', component: { template: '<div>Profile</div>' } },
        { path: '/messages', name: 'Messages', component: { template: '<div>Messages</div>' } },
        { path: '/create-product', name: 'Sell Items', component: { template: '<div>Sell</div>' } },
        { path: '/login', name: 'Login', component: { template: '<div>Login</div>' } },
        { path: '/register', name: 'Register', component: { template: '<div>Register</div>' } },
      ],
    })
  })

  it('renders footer sections with headings and links', async () => {
    const wrapper = mount(Footer, {
      global: {
        plugins: [router, i18n],
      },
    })

    // Check main headings
    expect(wrapper.find('#about-section').text()).toBe('About Us')
    expect(wrapper.find('#navigation-section').text()).toBe('Quick Links')
    expect(wrapper.find('#help-section').text()).toBe('Help & Support')
    expect(wrapper.find('#account-section').text()).toBe('Account')

    // Check about description
    expect(wrapper.text()).toContain('Some description about the company.')

    // Check social links
    const socialLinks = wrapper.findAll('.social-link')
    expect(socialLinks).toHaveLength(3)
    expect(socialLinks[0].text()).toBe('Instagram')
    expect(socialLinks[0].attributes('aria-label')).toBe('Follow us on Instagram')
    expect(socialLinks[1].text()).toBe('Facebook')
    expect(socialLinks[1].attributes('aria-label')).toBe('Follow us on Facebook')
    expect(socialLinks[2].text()).toBe('Twitter')
    expect(socialLinks[2].attributes('aria-label')).toBe('Follow us on Twitter')
  })

  it('renders the footer navigation links properly', async () => {
    const wrapper = mount(Footer, {
      global: {
        plugins: [router, i18n],
      },
    })

    // Profile, Messages, Sell Items
    const quickLinks = wrapper.find('#navigation-section')
    expect(quickLinks.exists()).toBe(true)
    expect(quickLinks.text()).toContain('Quick Links')

    // Inside that nav, we should have RouterLinks
    //  - Profile
    //  - Messages
    //  - Sell Items
    // Then in the help section we have normal anchors
    //  - FAQ, Contact Us, Terms of Service, Privacy Policy
    const footerLinks = wrapper.findAll('.footer-link')
    // This includes both router links and normal anchors
    // Let's check some subset of them
    const linkTexts = footerLinks.map((link) => link.text())
    expect(linkTexts).toContain('Profile')
    expect(linkTexts).toContain('Messages')
    expect(linkTexts).toContain('Sell Items')
    expect(linkTexts).toContain('FAQ')
    expect(linkTexts).toContain('Contact Us')
    expect(linkTexts).toContain('Terms of Service')
    expect(linkTexts).toContain('Privacy Policy')
    expect(linkTexts).toContain('Log In')
    expect(linkTexts).toContain('Register')
  })

  it('displays current year correctly in copyright', () => {
    const wrapper = mount(Footer, {
      global: {
        plugins: [router, i18n],
      },
    })

    const currentYear = new Date().getFullYear()
    expect(wrapper.text()).toContain(`© ${currentYear} Clozet.`)
  })
})
