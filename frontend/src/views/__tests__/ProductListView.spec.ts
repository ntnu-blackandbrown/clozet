import { mount, flushPromises } from '@vue/test-utils'
import ProductList from '@/views/ProductListView.vue'
import axios from 'axios'
import { describe, it, expect, beforeEach, vi, type Mocked } from 'vitest'

// Mock axios
vi.mock('axios')
const mockedAxios = axios as Mocked<typeof axios>

describe('ProductList', () => {
  const mockItems = [
    {
      id: 1,
      title: 'Product One',
      price: '100',
      category: 'Shoes',
      images: ['img1.jpg'],
      location: 'Oslo',
    },
    {
      id: 2,
      title: 'Product Two',
      price: '200',
      category: 'Hats',
      images: ['img2.jpg'],
      location: 'Bergen',
    },
  ]

  beforeEach(() => {
    mockedAxios.get.mockResolvedValue({ data: mockItems })
  })

  it('fetches and renders products', async () => {
    const wrapper = mount(ProductList, {
      global: {
        stubs: {
          ProductCard: {
            props: ['id', 'title', 'price', 'category', 'image', 'location'],
            template: `<div class="product-card-stub">{{ title }}</div>`,
          },
          ProductDisplayModal: true,
        },
      },
    })

    await flushPromises()
    const cards = wrapper.findAll('.product-card-stub')
    expect(cards.length).toBe(2)
    expect(cards[0].text()).toContain('Product One')
    expect(cards[1].text()).toContain('Product Two')
  })

  it('opens modal with correct product ID when card clicked', async () => {
    const wrapper = mount(ProductList, {
      global: {
        stubs: {
          ProductCard: {
            props: ['id', 'title'],
            emits: ['click'],
            template: `<div @click="$emit('click', id)" class="product-card-stub">{{ title }}</div>`,
          },
          ProductDisplayModal: {
            props: ['productId'],
            emits: ['close'],
            template: `<div class="modal-stub">Product ID: {{ productId }}</div>`,
          },
        },
      },
    })

    await flushPromises()
    await wrapper.findAll('.product-card-stub')[0].trigger('click')

    expect(wrapper.find('.modal-stub').exists()).toBe(true)
    expect(wrapper.find('.modal-stub').text()).toContain('Product ID: 1')
  })

  it('closes modal on @close', async () => {
    const wrapper = mount(ProductList, {
      global: {
        stubs: {
          ProductCard: {
            props: ['id', 'title'],
            emits: ['click'],
            template: `<div @click="$emit('click', id)" class="product-card-stub">{{ title }}</div>`,
          },
          ProductDisplayModal: {
            props: ['productId'],
            emits: ['close'],
            template: `<div class="modal-stub"><button @click="$emit('close')">Close</button></div>`,
          },
        },
      },
    })

    await flushPromises()
    await wrapper.findAll('.product-card-stub')[0].trigger('click')
    expect(wrapper.find('.modal-stub').exists()).toBe(true)

    await wrapper.find('.modal-stub button').trigger('click')
    await flushPromises()

    expect(wrapper.find('.modal-stub').exists()).toBe(false)
  })
})
