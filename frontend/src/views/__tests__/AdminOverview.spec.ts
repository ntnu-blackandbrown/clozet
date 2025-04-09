// frontend/src/views/admin/AdminOverview.spec.ts
import { describe, it, expect, vi, beforeEach } from 'vitest'
import type { MockedFunction } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import AdminOverview from '@/views/admin/AdminOverview.vue'
import { UserService } from '@/api/services/UserService'
import { ProductService } from '@/api/services/ProductService'
import { CategoryService } from '@/api/services/CategoryService'
import { TransactionService } from '@/api/services/TransactionService'
import { createRouter, createWebHistory } from 'vue-router' // Needed for RouterLink
import type { AxiosResponse } from 'axios' // Import AxiosResponse type

// Mock API services
vi.mock('@/api/services/UserService')
vi.mock('@/api/services/ProductService')
vi.mock('@/api/services/CategoryService')
vi.mock('@/api/services/TransactionService')

// Mock data
const mockUsers = [
  {
    id: 1,
    username: 'testuser1',
    firstName: 'Test',
    lastName: 'User1',
    email: 'test1@example.com',
    createdAt: '2023-10-26T10:00:00Z',
  },
  {
    id: 2,
    username: 'testuser2',
    firstName: 'Test',
    lastName: 'User2',
    email: 'test2@example.com',
    createdAt: '2023-10-25T10:00:00Z',
  },
  {
    id: 3,
    username: 'testuser3',
    firstName: 'Test',
    lastName: 'User3',
    email: 'test3@example.com',
    createdAt: '2023-10-27T10:00:00Z',
  }, // Ensure recent logic works
  {
    id: 4,
    username: 'testuser4',
    firstName: 'Test',
    lastName: 'User4',
    email: 'test4@example.com',
    createdAt: '2023-10-24T10:00:00Z',
  },
]
const mockProducts = [
  { id: 1, title: 'Item 1', price: 10.0, seller: 'Seller A', createdAt: '2023-10-26T11:00:00Z' },
  { id: 2, title: 'Item 2', price: 20.5, seller: 'Seller B', createdAt: '2023-10-25T11:00:00Z' },
  { id: 3, title: 'Item 3', price: 15.0, seller: 'Seller C', createdAt: '2023-10-27T11:00:00Z' },
  { id: 4, title: 'Item 4', price: 5.0, seller: 'Seller D', createdAt: '2023-10-24T11:00:00Z' },
]
const mockCategories = [
  { id: 1, name: 'Cat 1' },
  { id: 2, name: 'Cat 2' },
]
const mockTransactions = [
  { id: 1, amount: 100 },
  { id: 2, amount: 50 },
]

// Mock Router for RouterLink
const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: '/', component: { template: '<div></div>' } }], // Dummy route
})

describe('AdminOverview.vue', () => {
  beforeEach(() => {
    // Reset mocks before each test
    vi.resetAllMocks()

    // Helper to create a mock AxiosResponse
    const createMockResponse = <T>(data: T): AxiosResponse<T> => ({
      data,
      status: 200,
      statusText: 'OK',
      headers: {},
      config: {} as any, // Use 'as any' for simplicity if config details aren't needed
    })

    // Default successful mocks
    ;(UserService.getAllUsers as MockedFunction<typeof UserService.getAllUsers>).mockResolvedValue(
      createMockResponse(mockUsers),
    )
    ;(
      ProductService.getAllItems as MockedFunction<typeof ProductService.getAllItems>
    ).mockResolvedValue(createMockResponse(mockProducts))
    ;(
      CategoryService.getAllCategories as MockedFunction<typeof CategoryService.getAllCategories>
    ).mockResolvedValue(createMockResponse(mockCategories))
    ;(
      TransactionService.getAllTransactions as MockedFunction<
        typeof TransactionService.getAllTransactions
      >
    ).mockResolvedValue(createMockResponse(mockTransactions))
  })

  it('displays loading state initially', () => {
    const wrapper = mount(AdminOverview, {
      global: {
        plugins: [router], // Provide router instance
      },
    })
    expect(wrapper.find('.loading-spinner').exists()).toBe(true)
    expect(wrapper.text()).toContain('Loading dashboard data...')
  })

  it('displays statistics and recent data after successful fetch', async () => {
    const wrapper = mount(AdminOverview, {
      global: {
        plugins: [router],
      },
    })
    // Wait for API calls and component updates
    await flushPromises()

    // Check loading state is gone
    expect(wrapper.find('.loading-spinner').exists()).toBe(false)

    // Check statistic cards
    const stats = wrapper.findAll('.stat-card')
    expect(stats.length).toBe(4)
    expect(stats[0].text()).toContain('Total Users')
    expect(stats[0].text()).toContain(mockUsers.length)
    expect(stats[1].text()).toContain('Total Items')
    expect(stats[1].text()).toContain(mockProducts.length)
    expect(stats[2].text()).toContain('Categories')
    expect(stats[2].text()).toContain(mockCategories.length)
    expect(stats[3].text()).toContain('Transactions')
    expect(stats[3].text()).toContain(mockTransactions.length)

    // Check recent users table (top 3 sorted by date)
    const userRows = wrapper.findAll('.activity-card:first-of-type .data-table tbody tr')
    expect(userRows.length).toBe(3) // Shows top 3
    expect(userRows[0].text()).toContain('testuser3') // Most recent
    expect(userRows[1].text()).toContain('testuser1')
    expect(userRows[2].text()).toContain('testuser2')

    // Check recent items table (top 3 sorted by date)
    const itemRows = wrapper.findAll('.activity-card:last-of-type .data-table tbody tr')
    expect(itemRows.length).toBe(3) // Shows top 3
    expect(itemRows[0].text()).toContain('Item 3') // Most recent
    expect(itemRows[0].text()).toContain('15.00 kr')
    expect(itemRows[1].text()).toContain('Item 1')
    expect(itemRows[2].text()).toContain('Item 2')
  })
})
