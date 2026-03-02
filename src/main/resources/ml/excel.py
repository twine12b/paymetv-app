import pandas as pd
from datetime import datetime
from openpyxl import Workbook

# Create the data for the "Savings Tracker" sheet
savings_data = {
    "Date": [datetime(2025, 5, 31)],
    "Bank Name": ["Ally Bank"],
    "Account Type": ["High Yield"],
    "Account Number (Last 4)": ["1234"],
    "Starting Balance": [5000.00],
    "Deposits": [500.00],
    "Withdrawals": [0.00],
    "Ending Balance": [5500.00],
    "Interest Rate (%)": [4.25],
    "Notes": ["Monthly update"]
}
savings_df = pd.DataFrame(savings_data)

# Create the data for the "Interest Rate Changes" sheet
rate_changes_data = {
    "Date": [datetime(2025, 4, 15), datetime(2025, 2, 10)],
    "Bank Name": ["Ally Bank", "Marcus"],
    "Account Type": ["High Yield", "Online Savings"],
    "Old Rate (%)": [4.00, 3.85],
    "New Rate (%)": [4.25, 4.00],
    "Reason / Notes": ["Fed rate hike response", "Quarterly adjustment"]
}
rate_changes_df = pd.DataFrame(rate_changes_data)

# Save both sheets to an Excel file
excel_path = "c:\dumps\Savings_Account_Tracker.xlsx"
with pd.ExcelWriter(excel_path, engine='openpyxl') as writer:
    savings_df.to_excel(writer, sheet_name="Savings Tracker", index=False)
    rate_changes_df.to_excel(writer, sheet_name="Interest Rate Changes", index=False)

excel_path