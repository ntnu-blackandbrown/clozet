import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import TransactionManagement from '@/views/admin/transactions/TransactionManagement.vue'
import { TransactionService } from '@/api/services/TransactionService'

// Mock the TransactionService method
vi.mock('@/api/services/TransactionService', () => ({
  TransactionService: {
    getAllTransactions: vi.fn(),
  },
}))

describe('TransactionManagement', () => {
  // A sample list of transaction objects to use in tests
  const sampleTransactions = [
    {
      id: 1,
      item: { title: 'Item A' },
      buyerId: '101',
      sellerId: '201',
      amount: '100.50',
      paymentMethod: 'Credit Card',
      status: 'COMPLETED',
      createdAt: '2025-01-01T10:00:00Z',
    },
    {
      id: 2,
      item: { title: 'Item B' },
      buyerId: '102',
      sellerId: '202',
      amount: '200.75',
      paymentMethod: 'Vipps',
      status: 'PENDING',
      createdAt: '2025-02-01T12:00:00Z',
    },
    {
      id: 3,
      item: { title: 'Item C' },
      buyerId: '103',
      sellerId: '203',
      amount: '150.00',
      paymentMethod: 'PayPal',
      status: 'FAILED',
      createdAt: '2025-03-01T08:30:00Z',
    },
  ]

  beforeEach(() => {
    // Clear any mocks between tests
    vi.clearAllMocks()
  })

  it('displays an error message if fetching transactions fails', async () => {
    ;(TransactionService.getAllTransactions as any).mockRejectedValue(new Error('Network error'))
    const wrapper = mount(TransactionManagement)
    await flushPromises()

    expect(wrapper.text()).toContain('Failed to load transactions')
    // Check that a retry button is displayed in the error message
    expect(wrapper.find('.btn-secondary').exists()).toBe(true)
  })


  it('resets filters correctly', async () => {
    ;(TransactionService.getAllTransactions as any).mockResolvedValue({ data: sampleTransactions })
    const wrapper = mount(TransactionManagement)
    await flushPromises()

    // Apply filters
    const select = wrapper.find('select#status-filter')
    await select.setValue('FAILED')
    const searchInput = wrapper.find('input.search-input')
    await searchInput.setValue('item')
    await nextTick()

    let rows = wrapper.findAll('tbody tr')
    // Only one transaction with status "FAILED" (id 3) is expected
    expect(rows.length).toBe(1)

    // Find and click the "Reset Filters" button
    // There is a reset button inside the filter-container with text "Reset Filters"
    const resetButton = wrapper.find('button.btn-secondary')
    await resetButton.trigger('click')
    await nextTick()

    // After reset, all transactions should be displayed
    rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(sampleTransactions.length)
  })

  it('sorts transactions when a sortable header is clicked', async () => {
    ;(TransactionService.getAllTransactions as any).mockResolvedValue({ data: sampleTransactions })
    const wrapper = mount(TransactionManagement)
    await flushPromises()

    // Initially, the transactions are sorted by createdAt (desc) by default.
    // The first row (most recent date) should be the transaction with id 3.
    let firstRowId = wrapper.find('tbody tr td').text()
    expect(firstRowId).toBe('3')

    // Click the sortable header for "ID". The header is a th with a click handler.
    // Use a CSS selector matching the "ID" header; here we assume it is the first .sortable header.
    const idHeader = wrapper.find('th.sortable')
    await idHeader.trigger('click')
    await nextTick()

    // After clicking, sortKey should be set to "id" with sortDirection "desc",
    // meaning the highest id comes first. For our sample, that yields ordering: 3, 2, 1.
    let rows = wrapper.findAll('tbody tr')
    let ids = rows.map((row) => row.find('td').text())
    expect(ids).toEqual(['3', '2', '1'])

    // Click the "ID" header again to toggle sort direction to "asc"
    await idHeader.trigger('click')
    await nextTick()

    rows = wrapper.findAll('tbody tr')
    let idsAsc = rows.map((row) => row.find('td').text())
    expect(idsAsc).toEqual(['1', '2', '3'])
  })

  it('displays formatted currency and date correctly', async () => {
    ;(TransactionService.getAllTransactions as any).mockResolvedValue({ data: sampleTransactions })
    const wrapper = mount(TransactionManagement)
    await flushPromises()

    // Check that the currency is formatted as expected (e.g. "100.50 kr")
    expect(wrapper.html()).toContain('100.50 kr')
    // Check that the date is formatted (contains a date and time string)
    expect(wrapper.html()).toMatch(/\d{1,2}\/\d{1,2}\/\d{4} \d{1,2}:\d{2}/)
  })
})
