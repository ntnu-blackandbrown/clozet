import { setActivePinia, createPinia } from 'pinia'
import { useCategoryStore } from '@/stores/Category'
import axios from 'axios'
import { beforeEach, describe, expect, it, vi } from 'vitest'
vi.mock('axios')
const mockedAxios = axios as jest.Mocked<typeof axios>

describe('CategoryStore', () => {
    beforeEach(() => {
        setActivePinia(createPinia())
        vi.resetAllMocks()
    })

    it('fetches categories successfully', async () => {
      mockedAxios.get.mockResolvedValueOnce({
        data: [{
          id: 1,
          name: 'Test Category',
          description: 'Test Description',
          parentId: null,
          createdAt: '2025-01-01',
          updatedAt: '2025-01-01',
          subcategoryIds: [],
          parentName: null,
        }]
      })

      const store = useCategoryStore()

      await store.fetchCategories()

      expect(store.categories.length).toBe(1)
      expect(store.categories[0].name).toBe('Test Category')
      expect(store.error).toBeNull()
      expect(store.loading).toBe(false)
    })

    it('sets error on fetchCategories failure', async () => {
      mockedAxios.get.mockRejectedValueOnce(new Error('Network error'))
      const store = useCategoryStore()

      await store.fetchCategories()

      expect(store.error).toBe('Failed to fetch categories')
      expect(store.loading).toBe(false)
    })

    it('gets category by ID', async() => {
      mockedAxios.get.mockResolvedValueOnce({data: {id: 1, name: 'Test Category'}})
      const store = useCategoryStore()

      const result = await store.getCategoryById(1)
      expect(result.name).toBe('Test Category')
      expect(store.error).toBeNull()
    })
    it('sets error on getCategoryById failure', async () => {
      mockedAxios.get.mockRejectedValueOnce(new Error('Network error'))
      const store = useCategoryStore()

      await store.getCategoryById(1)

      expect(store.error).toBe('Failed to fetch category')
      expect(store.loading).toBe(false)
    })

    it('creates a category sucessfully', async () => {
      const category = {id: 2, name: 'Category 2'}
      mockedAxios.post.mockResolvedValueOnce({data: category})

      const store = useCategoryStore()
      const result = await store.createCategory(category)

      expect(result.name).toBe('Category 2')
      expect(store.error).toBeNull()
    })

    it('sets error on createCategory failure', async () => {
      const category = {id: 2, name: 'Category 2'}
      mockedAxios.post.mockRejectedValueOnce(new Error('Network error'))

      const store = useCategoryStore()
      await store.createCategory(category)

      expect(store.error).toBe('Failed to create category')
      expect(store.loading).toBe(false)
    })

    it('fetches top five categories', async() => {
      const mockTop5 = [{
        id: 1,
        name: 'Category 1',
      },
      {
        id: 2,
        name: 'Category 2',
      },
      {
        id: 3,
        name: 'Category 3',
      },
      {
        id: 4,
        name: 'Category 4',
      },
      {
        id: 5,
        name: 'Category 5',
      },
    ]

   mockedAxios.get.mockResolvedValueOnce({data: mockTop5})

   const store = useCategoryStore()
   const result = await store.getTopFiveCategories()

   expect(result.length).toBe(5)
   expect(result[0].name).toBe('Category 1')
   expect(store.error).toBeNull()
   expect(store.loading).toBe(false)
    })
    it('sets error on getTopFiveCategories failure', async () => {
      mockedAxios.get.mockRejectedValueOnce(new Error('Network error'))
      const store = useCategoryStore()

      await store.getTopFiveCategories()

    })
})