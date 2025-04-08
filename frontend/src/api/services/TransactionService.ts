import axios from '../axios'

export const TransactionService = {
  /**
   * Get all transactions (admin)
   */
  getAllTransactions: () => {
    return axios.get('/api/transactions')
  },

  /**
   * Get transactions for a specific buyer
   */
  getBuyerTransactions: (buyerId: number) => {
    return axios.get(`/api/transactions/buyer/${buyerId}`)
  },

  /**
   * Get transactions for a specific seller
   */
  getSellerTransactions: (sellerId: number) => {
    return axios.get(`/api/transactions/seller/${sellerId}`)
  },

  /**
   * Create a new purchase transaction
   */
  createPurchase: (transactionData: any) => {
    return axios.post('/api/transactions/purchase', transactionData)
  },
}

export default TransactionService
