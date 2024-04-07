# Author
Artsiom Siandzevich

# Files
- BasketSplitter.java
- Pair.java
- TestGenerator.java
- Main.java

# BasketSplitter.java

## Functions

- `BasketSplitter`: Two versions of the constructor, one for tests and one for the task.
- `split`: Separated into three steps: Filtering data, Finding solutions, Getting the best one.

### Filtering (Step 1)

- `FilteringBasketByCompanies`: Function to create a map with companies as keys, items that exist in the basket, and companies able to deliver.

### Finding solutions (Step 2)

- `QueueSplit`: Function to find solutions:
  - At the beginning, the queue is empty.
  - Putting into the queue every combination of companies.
  - Trying to add new companies to the current best combination.
  - Saving combinations when the set of items is equal to our basket.

### Getting optimal solution

- `CalculateOptimalSolution`: Function will be called only if there are more than one solution with the same number of companies.

### Pair.java
Class written myself to not include additional libraries.

# Compiled jar file
in folder `out\artifacts`
