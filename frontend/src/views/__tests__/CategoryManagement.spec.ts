// tests/unit/components/admin/categories/CategoryManagement.spec.ts
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import CategoryManagement from '@/views/admin/categories/CategoryManagement.vue'
import { CategoryService } from '@/api/services/CategoryService'

// Mock the CategoryService methods
vi.mock('@/api/services/CategoryService', () => ({
  CategoryService: {
    getAllCategories: vi.fn(),
    createCategory: vi.fn(),
    updateCategory: vi.fn(),
    deleteCategory: vi.fn(),
  },
}))

describe('CategoryManagement.vue', () => {
  let wrapper: VueWrapper<any>

  // Helper to mount the component
  const createWrapper = async () => {
    wrapper = mount(CategoryManagement)
    // Wait a tick for the onMounted hook (which calls fetchCategories)
    await wrapper.vm.$nextTick()
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    wrapper?.unmount()
  })

  it('renders the loading spinner while categories are being fetched', async () => {
    // Simulate getAllCategories never resolving (loading state)
    (CategoryService.getAllCategories as any).mockImplementation(() => new Promise(() => {}))
    await createWrapper()

    expect(wrapper.find('[role="status"]').exists()).toBe(true)
    expect(wrapper.text()).toContain('Loading categories...')
  })

  it('renders categories in a table once loaded', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({
      data: [
        { id: 1, name: 'Category A', description: 'Desc A', parent: null },
        {
          id: 2,
          name: 'Category B',
          description: 'Desc B',
          parent: { id: 1, name: 'Category A' },
        },
      ],
    })

    await createWrapper()

    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(2)
    expect(rows[0].text()).toContain('Category A')
    expect(rows[1].text()).toContain('Category B')
    expect(rows[1].text()).toContain('Category A') // Parent name
  })

  it('renders an empty state when no categories exist', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    expect(wrapper.text()).toContain('No categories found')
  })

  it('shows an error message if fetching categories fails', async () => {
    (CategoryService.getAllCategories as any).mockRejectedValueOnce(new Error('API error'))
    await createWrapper()
    expect(wrapper.text()).toContain('Failed to load categories')
  })

  it('allows retrying after load failure', async () => {
    (CategoryService.getAllCategories as any)
      .mockRejectedValueOnce(new Error('API error'))
      .mockResolvedValueOnce({ data: [] })

    await createWrapper()
    expect(wrapper.text()).toContain('Failed to load categories')

    // Click the "Retry" button
    await wrapper.find('button[aria-label="Retry loading categories"]').trigger('click')
    // Wait for the re-fetch and update
    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('No categories found')
  })

  it('opens the "Create Category" modal when the Add New Category button is clicked', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    const createModal = wrapper.findComponent({ name: 'CreateCategoryModal' })
    // Modal is initially hidden (is-visible prop should be false)
    expect(createModal.props('isVisible')).toBe(false)

    // Click "Add New Category" button
    await wrapper.find('button[aria-label="Add new category"]').trigger('click')
    expect(wrapper.findComponent({ name: 'CreateCategoryModal' }).props('isVisible')).toBe(true)
  })


  it('validates the edit form and prevents update when invalid', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({ data: [] })
    await createWrapper()
    wrapper.vm.showEditForm = true
    wrapper.vm.editCategoryForm = {
      id: 1,
      name: '', // Invalid: name is required
      description: 'A valid description',
      parentId: null,
    }
    await wrapper.vm.$nextTick()

    await wrapper.find('form.category-form').trigger('submit.prevent')

    expect(CategoryService.updateCategory).not.toHaveBeenCalled()
    expect(wrapper.vm.editFormErrors.name).toBe('Category name is required')
  })



  it('prompts for confirmation before deleting a category and aborts if cancelled', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({
      data: [{ id: 1, name: 'Category A', description: 'Desc A', parent: null }],
    })
    await createWrapper()

    global.confirm = vi.fn().mockReturnValueOnce(false)
    await wrapper.vm.deleteCategory(1)
    expect(global.confirm).toHaveBeenCalled()
    expect(CategoryService.deleteCategory).not.toHaveBeenCalled()
  })



  it('returns "-" if a category has no parent', async () => {
    (CategoryService.getAllCategories as any).mockResolvedValueOnce({
      data: [{ id: 1, name: 'Category A', description: 'Desc', parent: null }],
    })
    await createWrapper()
    const rowText = wrapper.find('tbody tr').text()
    expect(rowText).toContain('-')
  })
})
