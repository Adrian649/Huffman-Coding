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

	@Override
	public void add(E e) {
		/* Special case: Be careful when the new value is the smallest */
		Node<E> newNode = new Node<>(e);
		Node<E> curNode;
		if (this.currentSize == 0) {
			head = newNode;
		}else {
			curNode = head;
			while (curNode != null) {
				if (head.getValue().compareTo(e) >= 0) {
					newNode.setNext(head);
					head = newNode;
					break;
				}else {
					if (curNode.getNext() != null && curNode.getNext().getValue().compareTo(e) >= 0 ) {
						newNode.setNext(curNode.getNext());
						curNode.setNext(newNode);
					}
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

	@Override
	public boolean remove(E e) {
		/* Special case: Be careful when the value is found at the head node */
		Node<E> rmNode, curNode;
		rmNode = head;
		curNode = head.getNext();
		while (curNode != null) {
			if (head.getValue().compareTo(e) == 0) {
				head = head.getNext();
				this.currentSize--;
				return true;
			}else {
				if (curNode != null &&  curNode.getValue().compareTo(e) == 0) {
					rmNode.setNext(curNode.getNext());
					this.currentSize--;
					return true;
				}
			}
			rmNode = rmNode.getNext();
			curNode = curNode.getNext();
		}
		return false;
	}

	@Override
	public E removeIndex(int index) {
		/* TODO ADD CODE HERE */
		/* Special case: Be careful when index = 0 */
		Node<E> rmNode, curNode;
		rmNode = head;
		curNode = head.getNext();
		E value = null;
		int counter = 0;
		while (curNode != null) {
			if (index == 0) {
				value = head.getValue();
				head = head.getNext();
				this.currentSize--;
				return value;
			}else {
				if (counter + 1 == index) {
					value = curNode.getValue();
					rmNode.setNext(curNode.getNext());
					this.currentSize--;
					return value;
				}
			}
			rmNode = rmNode.getNext();
			curNode = curNode.getNext();
		}
		return value;
	}

	@Override
	public int firstIndex(E e) {
		int target = -1;
		Node<E> curNode = head;
		int index = 0;
		while (curNode != null) {
			if (curNode.getValue().compareTo(e) > 0) {
				break;
			}else {
				if (curNode.getValue().compareTo(e) == 0) {
					target = index;
					return index;
				}
			}
			curNode = curNode.getNext();
			index++;
		}
		
		return target; //Dummy Return
	}

	@Override
	public E get(int index) {
		Node<E> curNode = head;
		int counter = 0;
		while (curNode != null) {
			if (counter == index) {
				return curNode.getValue();
			}
			curNode = curNode.getNext();
			counter++;
		}
		return null; //Dummy Return
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
