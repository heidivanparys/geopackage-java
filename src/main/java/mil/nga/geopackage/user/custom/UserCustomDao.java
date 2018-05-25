package mil.nga.geopackage.user.custom;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackageException;
import mil.nga.geopackage.db.GeoPackageConnection;
import mil.nga.geopackage.user.UserDao;

/**
 * User Custom DAO for reading user custom data tables
 * 
 * @author osbornb
 * @since 3.0.1
 */
public class UserCustomDao
		extends
		UserDao<UserCustomColumn, UserCustomTable, UserCustomRow, UserCustomResultSet> {

	/**
	 * User Custom connection
	 */
	protected final UserCustomConnection userDb;

	/**
	 * Constructor
	 * 
	 * @param database
	 * @param db
	 * @param userDb
	 * @param table
	 */
	public UserCustomDao(String database, GeoPackageConnection db,
			UserCustomConnection userDb, UserCustomTable table) {
		super(database, db, userDb, table);

		this.userDb = userDb;
	}

	/**
	 * Constructor
	 * 
	 * @param dao
	 *            user custom data access object
	 */
	protected UserCustomDao(UserCustomDao dao) {
		this(dao.getDatabase(), dao.getDb(), dao.getUserDb(), dao.getTable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoundingBox getBoundingBox() {
		throw new GeoPackageException(
				"Bounding Box not supported for User Custom");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserCustomRow newRow() {
		return new UserCustomRow(getTable());
	}

	/**
	 * Get the User Custom connection
	 * 
	 * @return user custom connection
	 */
	public UserCustomConnection getUserDb() {
		return userDb;
	}

	/**
	 * Read the database table and create a DAO
	 * 
	 * @param database
	 *            database name
	 * @param connection
	 *            db connection
	 * @param tableName
	 *            table name
	 * @return user custom DAO
	 */
	public static UserCustomDao readTable(String database,
			GeoPackageConnection connection, String tableName) {

		UserCustomTableReader tableReader = new UserCustomTableReader(tableName);
		UserCustomConnection userDb = new UserCustomConnection(connection);
		UserCustomTable userCustomTable = tableReader.readTable(userDb);
		userDb.setTable(userCustomTable);
		UserCustomDao dao = new UserCustomDao(database, connection, userDb,
				userCustomTable);

		return dao;
	}

}