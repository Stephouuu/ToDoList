package fr.todolist.todolist.interfaces;

/**
 * Created by Stephane on 25/01/2017.
 */

/**
 * The image grid interface
 */
public interface ImageGridInterface {

    /**
     * When the user want to delete it
     * @param toDelete The path of the photo to delete
     */
    void onDeleteClick(String toDelete);

    /**
     * When the user want to add a new image
     */
    void onAddButtonClick();

    /**
     * When the user click on a image
     * @param photo The photo
     */
    void onItemClick(String photo);

}
