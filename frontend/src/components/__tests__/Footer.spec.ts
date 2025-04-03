import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import Footer from '@/components/layout/Footer.vue' // Adjust the path if needed

// Stub RouterLink so that it renders as a simple anchor element.
const RouterLinkStub = {
  name: 'RouterLink',
  props: ['to'],
  template: '<a class="router-link"><slot /></a>',
}

describe('Footer.vue', () => {
  it('renders the About section with correct text', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
      },
    })
    // Find the About section based on its heading text.
    const aboutSection = wrapper.find('.footer-section:nth-child(1)')
    expect(aboutSection.exists()).toBe(true)
    expect(aboutSection.text()).toContain('About Clozet')
    expect(aboutSection.text()).toContain('Your sustainable fashion marketplace')
  })

  it('renders Quick Links with RouterLink components', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
      },
    })
    // Assuming Quick Links is the second .footer-section in the grid.
    const quickLinksSection = wrapper.findAll('.footer-section')[1]
    expect(quickLinksSection.text()).toContain('Quick Links')

    // Check that it contains three RouterLink components with expected text.
    const links = quickLinksSection.findAllComponents(RouterLinkStub)
    expect(links.length).toBe(3)
    const linkTexts = links.map((link: any) => link.text())
    expect(linkTexts).toEqual(['My Profile', 'Messages', 'Sell Items'])
  })

  it('renders Help & Support with anchor links', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
      },
    })
    // Assuming Help & Support is the third .footer-section.
    const helpSection = wrapper.findAll('.footer-section')[2]
    expect(helpSection.text()).toContain('Help & Support')
    // Find all anchor links in this section.
    const anchors = helpSection.findAll('a.footer-link')
    expect(anchors.length).toBe(4)
    const anchorTexts = anchors.map((anchor) => anchor.text())
    expect(anchorTexts).toEqual(['FAQ', 'Contact Us', 'Terms of Service', 'Privacy Policy'])
  })

  it('renders Newsletter section with input and subscribe button', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
      },
    })
    // Assuming Newsletter is the fourth .footer-section.
    const newsletterSection = wrapper.findAll('.footer-section')[3]
    expect(newsletterSection.text()).toContain('Newsletter')
    expect(newsletterSection.text()).toContain('Subscribe to get updates')
    const input = newsletterSection.find('input[type="email"]')
    expect(input.exists()).toBe(true)
    const button = newsletterSection.find('button')
    expect(button.exists()).toBe(true)
    expect(button.text()).toBe('Subscribe')
  })

  it('renders footer bottom with the current year', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
        },
      },
    })
    const footerBottom = wrapper.find('.footer-bottom p')
    const currentYear = new Date().getFullYear().toString()
    expect(footerBottom.text()).toContain(currentYear)
  })
})
