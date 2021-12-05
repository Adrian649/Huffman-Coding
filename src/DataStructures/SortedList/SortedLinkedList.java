package SortedList;


/**
 * Implementation of a SortedList using a SinglyLinkedList
 * @author Fernando J. Bermudez & Juan O. Lopez
 * @author Adrian Irizarry Negron
 * @version 2.0
 * @since 10/16/2021
 */
public class SortedLinkedList<E extends Comparable<? super E>> extends AbstractSortedList<E> {

	@SuppressWarnings("unused")
	private static class Node<E> {

		private E value;
		private Node<E> next;

		public Node(E value, Node<E> next) {
			this.value = value;
			this.next = next;
		}

		public Node(E value) {
			this(value, null); // Delegate to other constructor
		}

		public Node() {
			this(null, null); // Delegate to other constructor
		}

		public E getValue() {
			return value;
		}

		public void setValue(E value) {
			this.value = value;
		}

		public Node<E> getNext() {
			return next;
		}

		public void setNext(Node<E> next) {
			this.next = next;
		}

		public void clear() {
			value = null;
			next = null;
		}				
	} // End of Node class

	
	private Node<E> head; // First DATA node (This is NOT a dummy header node)
	
	public SortedLinkedList() {
		head = null;
		currentSize = 0;
	}

	/**
	 * Adds a value to the sorted linked list making sure it doesn't break the order.
	 * @param e Value to be added to the linked list.
	 */
	@Override
	public void add(E e) {
		/* Special case: Be careful when the new value is the smallest */
		Node<E> newNode = new Node<>(e);
		Node<E> curNode;
		/* When the list is empty,the new node will be the head of the list */
		if (this.currentSize == 0) {
			head = newNode;
		}else {
			curNode = head;
			while (curNode != null) {
				/* If the new value is the smallest in the list now we need to make it the new head of the list */
				if (head.getValue().compareTo(e) >= 0) {
					newNode.setNext(head);
					head = newNode;
					break;
				}else {
					/* We look if the next value is bigger, if it is it means that the value that we are trying to add should go
					* in between curNode and curNode.getNext()
					*/
					if (curNode.getNext() != null && curNode.getNext().getValue().compareTo(e) >= 0 ) {
						newNode.setNext(curNode.getNext());
						curNode.setNext(newNode);
						break;
					}
					/* If curNode.getNext() is null it means we have reached the end of the linked list meaning the new value is our biggest value
					* and that this value should go at the very end of the list.
					*/
					if (curNode.getNext() == null) {
						curNode.setNext(newNode);
						break;
					}
				}
				curNode = curNode.getNext();
			}
		}
		this.currentSize++;
	}

	/**
	 * Removes the specified value. In the case of duplicates it only removes the first occurrence.
	 * @param e Value to be removed from the linked list.
	 * @return Returns boolean indicating if the value was removed or not.
	 */
	@Override
	public boolean remove(E e) {
		/* Special case: Be careful when the value is found at the head node */
		Node<E> rmNode, curNode;
		rmNode = head;
		curNode = head.getNext();
		while (curNode != null) {
			/* If the specified value is the head we need to make sure we properly remove it as its a special case
			* as we will need to assign a new value to the head. */
			if (head.getValue().compareTo(e) == 0) {
				head = head.getNext();
				this.currentSize--;
				return true;
			}else {
				/* We use curNode to get the value ahead of rmNode, if curNode is the value to be removed we simply set the previous
				* value (rmNode) next to curNode.getNext() */
				if (curNode != null &&  curNode.getValue().compareTo(e) == 0) {
					rmNode.setNext(curNode.getNext());
					this.currentSize--;
					return true;
				}
			}
			rmNode = rmNode.getNext();
			curNode = curNode.getNext();
		}
		return false; // Returns false if the value to be removed wasn't found.
	}

	/**
	 * Removes the value at the specified position.
	 * @param index Value of the position to be removed.
	 * @return The value that was removed.
	 */
	@Override
	public E removeIndex(int index) {
		/* Special case: Be careful when index = 0 */
		Node<E> rmNode, curNode;
		rmNode = head;
		curNode = head.getNext();
		E value = null;
		int counter = 0;
		while (rmNode != null) {
			/* When the index is 0, we need to remove the head, meaning we would need to assign head a new value */
			if (index == 0) {
				value = head.getValue();
				head = head.getNext();
				this.currentSize--;
				return value;
			}else {
				/* When the counter equals the index it means we have found the position we want to remove. */
				if (counter + 1 == index) {
					value = curNode.getValue();
					rmNode.setNext(curNode.getNext());
					this.currentSize--;
					return value;
				}
			}
			rmNode = rmNode.getNext();
			curNode = curNode.getNext();
			counter++;
		}
		return value; // Returns null if the position was out of bounds.
	}

	/**
	 * Returns the position of the first instance of the given value.
	 * @param e Value to search on the list.
	 * @return Position of the first occurrence of the given value.
	 */
	@Override
	public int firstIndex(E e) {
		int target = -1;
		Node<E> curNode = head;
		int index = 0;
		while (curNode != null) {
			/* If curNode value is bigger than the value we are looking for it means the value is not in the list
			* because the list is in order. */
			if (curNode.getValue().compareTo(e) > 0) {
				break;
			}else {
				/* If the value in the list equals the value to be looked for it means we have found it, therefore we return index */
				if (curNode.getValue().compareTo(e) == 0) {
					target = index;
					return index;
				}
			}
			curNode = curNode.getNext();
			index++;
		}
		
		return target; // Returns -1 if the value is not found within the list.
	}

	/**
	 * Gets the value at the given position.
	 * @param index Position to be searched.
	 * @return Value at the given index.
	 */
	@Override
	public E get(int index) {
		Node<E> curNode = head;
		int counter = 0;
		while (curNode != null) {
			/* When the counter equals the index it means we have reach the wanted position so we return the value there. */
			if (counter == index) {
				return curNode.getValue();
			}
			curNode = curNode.getNext();
			counter++;
		}
		return null; // Returns null if the index is out of bounds.
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public E[] toArray() {
		int index = 0;
		E[] theArray = (E[]) new Comparable[size()]; // Cannot use Object here
		for(Node<E> curNode = this.head; index < size() && curNode  != null; curNode = curNode.getNext(), index++) {
			theArray[index] = curNode.getValue();
		}
		return theArray;
	}

}
